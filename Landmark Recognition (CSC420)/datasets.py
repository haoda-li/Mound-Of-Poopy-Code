# %load datasets.py
import torchvision.transforms as tt
from torch.utils.data import Dataset
import torch
from PIL import Image

class GDataset(Dataset):
    def __init__(self, df, directory):

        self.df = df
        self.dir = directory
        self.transform = tt.Compose([
            tt.Resize((128, 128)),
            tt.ToTensor(),
            tt.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))
        ])
        
    def randBrightness(self, image):
        brightness = np.random.uniform(0.7, 1.3)
        jitter = brightness * np.array(image).astype(np.float32)
        jitter = np.clip(jitter, 0, 255).astype(np.uint8)
        jittered = Image.fromarray(jitter)
        return jittered
    
    def randCrop(self, image):
        code = np.random.randint(0, 5)
        scale = np.random.uniform(0.2, 0.4)
        width, height = image.size

        img_crop = np.array(image)

        if code == 0:
            img_crop = img_crop[int(height*scale):, :, :]

        elif code == 1:
            img_crop = img_crop[0:int(-height*scale), :, :]

        elif code == 2:
            img_crop = img_crop[:,int(width*scale):, :]

        elif code == 3:
            img_crop = img_crop[:, 0:int(-width*scale), :]

        # Upsample to original shape
        img_crop = cv2.resize(img_crop, (width, height))

        return Image.fromarray(img_crop)
    
    def __len__(self):
        return len(self.df) * 3

    
    def __getitem__(self, index):
        index, aug = index // 3, index % 3
        id_ = self.df.iloc[index]['id']
        img_name = "/".join([self.dir, id_+".jpg"])
        image = Image.open(img_name).convert('RGB')
        if aug == 1:
            image = self.randCrop(image)
        elif aug == 2:
            image = self.randBrightness(image)
        image = self.transform(image)
        return image, torch.tensor(int(self.df.iloc[index]['class_id'])).long()
        

class InferenceDataset(GDataset):
    def __init__(self, df, directory):
        super().__init__(df, directory)

    def __len__(self):
        return len(self.df)
    
    def __getitem__(self, index):
        
        id_ = self.df.iloc[index]['id']
        img_name = "/".join([self.dir, id_+".jpg"])
        image = Image.open(img_name).convert('RGB')
        return image, torch.tensor(int(self.df.iloc[index]['class_id'])).long()
