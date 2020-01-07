import pandas as pd
from torch.utils.data import DataLoader
import torch
import numpy as np
import torchvision.transforms as tt

from vgg16 import VGG16
from datasets import InferenceDataset, GDataset
import sys


def train_model(index, dir_, model_file="", 
                epochs=32, 
                model_save=False, model_save_name="vgg16_iter"):
    """ Training the model
        @param index: index file of the train data
        @param dir_: directory name that stores the properly formatted data
        @param model_file: model state file for continuous training
        @param epochs: number of iterations to run
        @param model_save: whether to save as a model file
        @param model_save_name: name of the saving model file
    """
    
    df = pd.read_csv(index).sample(frac=0.1)
    dataset = GDataset(df, dir_)
    
    batch_size = 108
    dataloader = DataLoader(dataset, batch_size=batch_size)

    model = VGG16()
    
    if model_file != "":
        model.load_state_dict(torch.load(model_file))
        for param in model.features[:34].parameters():
            param.requires_grad = False
    
    model.cuda()
    
    criterion = torch.nn.CrossEntropyLoss()
    softmax = torch.nn.Softmax(dim=1)
    optimizer = torch.optim.Adam(model.parameters())

        # train the model
    for e in range(epochs):

        train_loss = 0
        accuracy = 0
        counter = 0
        for x, t in dataloader:
            counter += 1
            print(round(counter / len(dataloader) * 100, 2), "%  ", end="\r")
            x, t = x.cuda(), t.cuda()
            optimizer.zero_grad()
            z = model(x)
            loss = criterion(z, t)
            loss.backward()
            optimizer.step()
            train_loss += loss.item()

            y = softmax(z)
            top_p, top_class = y.topk(1, dim=1)
            accuracy += (top_class[:, 0] == t).sum().item()

        print(e, train_loss / len(dataloader), accuracy / len(dataset))
        if model_save:
            torch.save(model.state_dict(), model_save_name + str(e) +".pth")
    

    
    
def inference(index, dir_, model_file):
    """ Inferencing the model
        @param index: index file of the train data
        @param dir_: directory name that stores the properly formatted data
        @param model_file: model state file for continuous training
    """
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

    loss_sum = 0
    accuracy = 0
    top_5_accuracy = 0
    counter = 0
    for x, t_cpu in dataset:
        counter += 1
        x_cpu = np.array(x)
        x, t = transform(x).unsqueeze(0).cuda(), t_cpu.unsqueeze(0).cuda()
        z = model(x)
        y = softmax(z)
        top_p, top_class = y.topk(5, dim=1)
        accuracy += (top_class[:, 0] == t).sum().item()
        top_5_accuracy += (top_class == t.unsqueeze(1).repeat(1, 5)).max(axis=1).values.sum().item()
    
    print("Top 1 Accuracy:", accuracy / len(dataset))
    print("Top 5 accuracy:", top_5_accuracy / len(dataset))
    
if __name__ == "__main__":
    print("Loading dataset:", sys.argv[2])
    print("Dataset folder:", sys.argv[3])
    print("Model:", sys.argv[4])
    mode = sys.argv[1]
    index = sys.argv[2]
    dir_ = sys.argv[3]
    model_file = sys.argv[4] if sys.argv[4] else ""
    if mode == "inference":
        inference(index, dir_, model_file)
    elif mode == "train":
        train_model(index, dir_, model_file=model_file, epochs=32, model_save=True)
    



