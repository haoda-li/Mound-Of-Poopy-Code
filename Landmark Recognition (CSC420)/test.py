import pandas as pd
from torch.utils.data import DataLoader
import torch
import numpy as np
import torchvision.transforms as tt
from PIL import Image

from vgg16 import VGG16
from datasets import InferenceDataset, GDataset
import sys, time
from ransac_matching import *

import argparse

def test_hard_thresholding(index, dir_, model_file):
    start = time.time()

    df = pd.read_csv(index)
    dataset = InferenceDataset(df, dir_)

    model = VGG16()
    model.load_state_dict(torch.load(model_file))
    model.cuda()
    model.eval()

    criterion = torch.nn.CrossEntropyLoss()
    softmax = torch.nn.Softmax(dim=1)
    transform = tt.Compose([
            tt.Resize((128, 128)),
            tt.ToTensor(),
            tt.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))
        ])

    accuracy = 0
    for x, t_cpu in dataset:

        x_cpu = np.array(x)
        x, t = transform(x).unsqueeze(0).cuda(), t_cpu.unsqueeze(0).cuda()
        z = model(x)
        y = softmax(z)
        top_p, top_class = y.topk(1, dim=1)
        if top_p[:, 0].item() < 0.45:
            accuracy += t_cpu.item() == 1000
            print(t_cpu.item(), 1000)
        else:
            accuracy += (top_class[:, 0] == t).sum().item()
            print(t_cpu.item(), top_class[:, 0].item())

    print("Top 1 Accuracy:", accuracy / len(dataset))
    end = time.time()
    print("Time:", end - start)
    
    
    
def test_ransac_surf(index, dir_, model_file, class_sample, class_folder):
    start = time.time()
    
    df = pd.read_csv(index)
    
    model = VGG16()
    model.load_state_dict(torch.load(model_file))
    model.cuda()
    model.eval()

    criterion = torch.nn.CrossEntropyLoss()
    softmax = torch.nn.Softmax(dim=1)
    transform = tt.Compose([
            tt.Resize((128, 128)),
            tt.ToTensor(),
            tt.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))
        ])
    test_index = pd.read_csv(class_sample)
    counter = 0
    for row in df.iterrows():
        id_ = row[1]['id']
        img_name = "/".join([dir_, id_+".jpg"])
        image = Image.open(img_name).convert('RGB')
        x = transform(image).unsqueeze(0).cuda()
        t = torch.tensor(row[1]['class_id']).long().unsqueeze(0).cuda()
        z = model(x)
        y = softmax(z)
        top_p, top_class = y.topk(5, dim=1)
        if top_p[0, 0].item() > 0.75:
            print(top_class[0, 0].item())
            counter += top_class[0, 0].item() == row[1]['class_id']
        else:
            top_class = randsac_matching_metrics(top_class[0].tolist(), 
                                                           img_name, 
                                                           test_index, 
                                                           class_folder)
            print(top_class)
            counter += top_class == row[1]['class_id']
    print("Accuracy:", counter / len(df))
    end = time.time()
    print("Time:", end - start)
    
    
def query_image(img_name, model_file, class_sample, class_folder, mode):
    start = time.time()
    
    model = VGG16()
    model.load_state_dict(torch.load(model_file))
    model.cuda()
    model.eval()

    criterion = torch.nn.CrossEntropyLoss()
    softmax = torch.nn.Softmax(dim=1)
    transform = tt.Compose([
            tt.Resize((128, 128)),
            tt.ToTensor(),
            tt.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))
        ])
    if mode == "query":
        test_index = pd.read_csv(class_sample)
    image = Image.open(img_name).convert('RGB')
    x = transform(image).unsqueeze(0).cuda()
    z = model(x)
    y = softmax(z)
    top_p, top_class = y.topk(5, dim=1)
    if mode == "query_thresh":
        if top_p[0, 0].item() > 0.45:
            top_class = top_class[0, 0].item()
        else:
            top_class = 1000
    else:
        if top_p[0, 0].item() > 0.75:
            top_class = top_class[0, 0].item()
        else:
            top_class = randsac_matching_metrics(top_class[0].tolist(), 
                                                 img_name, 
                                                 test_index, 
                                                 class_folder)
    print("Prediction", top_class)
    end = time.time()
    print("Time:", end - start)
    
    
if __name__ == "__main__":
    
    parser = argparse.ArgumentParser(description='Run test set or query image.')
    parser.add_argument('mode', help='choose the task among [thresh | ransac | query]')
    parser.add_argument('dataset', metavar='d', help='path to the dataset index csv file or the path to the query image')
    parser.add_argument('folder', metavar='f', help='path to the dataset folder')
    parser.add_argument('model', metavar='m', help='path to the model state dict')
    parser.add_argument('class_sample', help='path to the class sampling csv file')
    parser.add_argument('class_folder', help='path to the class sampling folder')
    
    args = parser.parse_args()
  
    

    index = args.dataset
    dir_ = args.folder
    model_file = args.model
    
    print("Loading dataset:", index)
    print("Dataset folder:", dir_)
    print("Model:", model_file)
    
    if args.mode == "thresh":
        test_hard_thresholding(index, dir_, model_file)
    elif args.mode == "ransac" and args.class_sample and args.class_folder: 
        test_ransac_surf(index, dir_, model_file, args.class_sample, args.class_folder)
    elif args.mode == "query" and args.class_sample and args.class_folder:
        query_image(index, model_file, args.class_sample, args.class_folder, args.mode)
    elif args.mode == "query_thresh":
        query_image(index, model_file, "", "", args.mode)
    

