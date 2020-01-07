import pandas as pd

import os
import numpy as np
import torchvision
from PIL import Image

import torch
from torchvision import models
from torch.utils.data import DataLoader
from datasets import GDataset

train = pd.read_csv("./train_2.csv")
train_data = GDataset(train, "train")


batch_size = 184
train_loader = DataLoader(train_data, batch_size=batch_size, shuffle=True)

model = models.vgg16_bn(pretrained=True)
# model = models.vgg16(pretrained=False)
model.avgpool = torch.nn.AdaptiveAvgPool2d(output_size=(3, 3))
model.classifier = torch.nn.Sequential(torch.nn.Linear(4608, 4096),
                                       torch.nn.ReLU(),
                                       torch.nn.Linear(4096, 4096),
                                       torch.nn.ReLU(),
                                       torch.nn.Linear(4096, train['class_id'].max() + 1))
# model.load_state_dict(torch.load("model15.pth"))


for param in model.features[:22].parameters():
    param.requires_grad = False
model = model.cuda()

criterion = torch.nn.CrossEntropyLoss()

optimizer = torch.optim.Adam(model.parameters())

epochs = 50

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
