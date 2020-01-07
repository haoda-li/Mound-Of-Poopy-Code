import pandas as pd
import torchvision.transforms as tt
from torch.utils.data import Dataset
import os
import numpy as np
import torchvision
from PIL import Image

import torch
from torchvision import models
from torch.utils.data import DataLoader


class GDataset(Dataset):
    def __init__(self, df, directory):

        self.df = df
        self.dir = directory
        self.transform = tt.Compose([
            tt.RandomCrop(224),
            tt.ColorJitter(),
            tt.ToTensor(),
            tt.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))
        ])
    
    
    def __len__(self):
        return len(self.df)

    
    def __getitem__(self, index): 
        id_ = self.df.iloc[index]['id']
        img_name = "/".join([self.dir, id_+".jpg"])
        image = Image.open(img_name)
        image = self.transform(image)
        return image, torch.tensor(int(self.df.iloc[index]['class_id'])).long()

    
train = pd.read_csv("./dataset2.csv")
ds = GDataset(train, "train")
model = models.vgg16_bn(pretrained=True)
batch_size = 48
train_loader = DataLoader(ds, batch_size=batch_size, shuffle=True)

model.classifier = torch.nn.Sequential(torch.nn.Linear(25088, 4096),
                                       torch.nn.ReLU(),
                                       torch.nn.Dropout(p=0.5),
                                       torch.nn.Linear(4096, 4096),
                                       torch.nn.ReLU(),
                                       torch.nn.Dropout(p=0.5),
                                       torch.nn.Linear(4096, train['class_id'].max() + 1))

model = model.cuda()

criterion = torch.nn.CrossEntropyLoss()

optimizer = torch.optim.Adam(model.parameters())

epochs = 500

softmax = torch.nn.Softmax(dim=1)

# train the model
for e in range(epochs):

    train_loss = 0
    accuracy = 0
    counter = 0
    for x, t in train_loader:
        counter += 1
        print(round(counter / len(train_loader), 2), end="\r")
        x, t = x.cuda(), t.cuda()
        optimizer.zero_grad()
        z = model(x)
        loss = criterion(z, t)
        loss.backward()
        optimizer.step()
        train_loss += loss.item()

        y = softmax(z)
        top_p, top_class = y.topk(1, dim=1)
        accuracy += (top_class[:, 0] == t).sum().item() / batch_size


    print(e, train_loss / len(train_loader), accuracy / len(train_loader))
    torch.save(model.state_dict(), "model" + str(e) +".pth")
    if e > 20:
        os.remove("model" + str(e-20) +".pth")