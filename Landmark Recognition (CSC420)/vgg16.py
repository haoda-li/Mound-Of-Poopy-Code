import torch.nn as nn
import torch

class VGG16(nn.Module):
    
    def __init__(self, num_classes=1000):
        super(VGG16, self).__init__()
        
        self.features = nn.Sequential(
            *self.getBNConv(3, 64), 
            *self.getBNConv(64, 64), 
            nn.MaxPool2d(kernel_size=2, stride=2, padding=0, dilation=1, ceil_mode=False), 
            *self.getBNConv(64, 128), 
            *self.getBNConv(128, 128), 
            nn.MaxPool2d(kernel_size=2, stride=2, padding=0, dilation=1, ceil_mode=False), 
            *self.getBNConv(128, 256),
            *self.getBNConv(256, 256), 
            *self.getBNConv(256, 256), 
            nn.MaxPool2d(kernel_size=2, stride=2, padding=0, dilation=1, ceil_mode=False), 
            *self.getBNConv(256, 512), 
            *self.getBNConv(512, 512), 
            *self.getBNConv(512, 512), 
            nn.MaxPool2d(kernel_size=2, stride=2, padding=0, dilation=1, ceil_mode=False), 
            *self.getBNConv(512, 512), 
            *self.getBNConv(512, 512), 
            *self.getBNConv(512, 512), 
            nn.MaxPool2d(kernel_size=2, stride=2, padding=0, dilation=1, ceil_mode=False)
        )
        
        self.avgpool = nn.AdaptiveAvgPool2d((3, 3))
        self.classifier = nn.Sequential(
            torch.nn.Linear(4608, 4096),
            torch.nn.ReLU(),
            torch.nn.Dropout(p=0.5),
            torch.nn.Linear(4096, 4096),
            torch.nn.ReLU(),
            torch.nn.Linear(4096, num_classes))

    def getBNConv(self, input_c, output_c):
        return (
            nn.Conv2d(input_c, output_c, 
                      kernel_size=(3, 3), stride=(1, 1), padding=(1, 1)),
            nn.BatchNorm2d(output_c, 
                           eps=1e-05, momentum=0.1, 
                           affine=True, track_running_stats=True),
            nn.ReLU(inplace=True)
        )
        
        
    def forward(self, x):
        x = self.features(x)
        x = self.avgpool(x)
        x = torch.flatten(x, 1)
        x = self.classifier(x)
        return x