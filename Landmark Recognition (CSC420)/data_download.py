# ThIS CODE ARE NOT WRITTEN BY OURSELVES
# it comes from 
# https://www.kaggle.com/sermakarevich/download-resize-clean-12-hours-44gb
# with some modification to fit our purpose
# Run this code to 

import logging 
import math
import os
import subprocess
from multiprocessing import Pool
from PIL import Image

def create_logger(filename, 
                  logger_name='logger', 
                  file_fmt='%(asctime)s %(levelname)-8s: %(message)s',
                  console_fmt='%(asctime)s | %(message)s',
                  file_level=logging.DEBUG, 
                  console_level=logging.INFO):
    
    logger = logging.getLogger(logger_name)
    logger.setLevel(logging.DEBUG)
    logger.propagate = False

    file_fmt = logging.Formatter(file_fmt)
    log_file = logging.FileHandler(filename)
    log_file.setLevel(file_level)
    log_file.setFormatter(file_fmt)
    logger.addHandler(log_file)

    console_fmt = logging.Formatter(console_fmt)
    log_console = logging.StreamHandler()
    log_console.setLevel(logging.DEBUG)
    log_console.setFormatter(console_fmt)
    logger.addHandler(log_console)

    return logger


def move_images_from_sub_to_root_folder(root_folder, subfolder):
    subfolder_content = os.listdir(subfolder)
    folders_in_subfolder = [i for i in subfolder_content if os.path.isdir(os.path.join(subfolder, i))]
    for folder_in_subfolder in folders_in_subfolder:
        subfolder_ = os.path.join(subfolder, folder_in_subfolder)
        move_images_from_sub_to_root_folder(root_folder, subfolder_)
    images = [i for i in subfolder_content if i not in folders_in_subfolder]
    for image in images:
        path_to_image = os.path.join(subfolder, image) 
        os.system(f"mv {path_to_image} ./{root_folder}/{image}")
        
        
def remove_all_subfolders_inside_folder(folder):
    folder_content = os.listdir(folder)
    subfolders = [i for i in folder_content if os.path.isdir(os.path.join(folder, i))]
    for subfolder in subfolders:
        path_to_subfolder = os.path.join(folder, subfolder)
        os.system(f'rm -r {path_to_subfolder}')
        
        
def resize_folder_images(src_dir, dst_dir, size=256):
    if not os.path.isdir(dst_dir):
        logger.info("destination directory does not exist, creating destination directory.")
        os.makedirs(dst_dir)

    image_filenames=os.listdir(src_dir)
    count = 0
    for filename in image_filenames:
        dst_filepath = os.path.join(dst_dir, filename)
        src_filepath = os.path.join(src_dir, filename)
        if filename not in indexes:
            new_img = None
        else:
            new_img = read_and_resize_image(src_filepath, size)
        if new_img is not None:
            new_img = new_img.convert("RGB")
            new_img.save(dst_filepath)
            count += 1
    logger.debug(f'{src_dir} files resized: {count}')
    
    
def read_and_resize_image(filepath, size):
    img = read_image(filepath)
    if img:
        img = resize_image(img, size)
    return img


def resize_image(img, size):
    if type(size) == int:
        size = (size, size)
    if len(size) > 2:
        raise ValueError("Size needs to be specified as Width, Height")
    return resize_contain(img, size)


def read_image(filepath):
    try:
        img = Image.open(filepath)
        return img
    except (OSError, Exception) as e:
        logger.debug("Can't read file {}".format(filepath))
        return None


def resize_contain(image, size, resample=Image.LANCZOS, bg_color=(255, 255, 255, 0)):
    img_format = image.format
    img = image.copy()
    img.thumbnail((size[0], size[1]), resample)
    background = Image.new('RGBA', (size[0], size[1]), bg_color)
    img_position = (
        int(math.ceil((size[0] - img.size[0]) / 2)),
        int(math.ceil((size[1] - img.size[1]) / 2))
    )
    background.paste(img, img_position)
    background.format = img_format
    return background.convert('RGB')
    
    
def download_resize_clean(index):
    try:
        if not os.path.exists('train'):
            os.system('mkdir train')

        file_index = '{0:0>3}'.format(index)
        images_file_name = f'images_{file_index}.tar'
        images_folder = images_file_name.split('.')[0]
        images_md5_file_name = f'md5.images_{file_index}.txt'
        images_tar_url = f'https://s3.amazonaws.com/google-landmark/train/{images_file_name}'
        images_md5_url = f'https://s3.amazonaws.com/google-landmark/md5sum/train/{images_md5_file_name}'

        logger.info(f'Downloading: {images_file_name} and {images_md5_file_name}')
        os.system(f'wget {images_tar_url}')
        os.system(f'wget {images_md5_url}')

        logger.debug(f'Checking file md5 and control md5')
        p = subprocess.Popen(
            ["md5sum", images_file_name], 
            stdout=subprocess.PIPE, 
            stderr=subprocess.STDOUT
        )
        stdout, stderr = p.communicate()
        md5_images = stdout.decode("utf-8").split(' ')[0]
        md5_control = open(images_md5_file_name).read().split(' ')[0]

        if md5_images == md5_control:
            logger.debug(f'MD5 are the same: {md5_images}, {md5_control}')
            logger.debug(f'Unarchiving images into: {images_folder}')
            os.system(f'mkdir {images_folder}')
            os.system(f'tar -xf {images_file_name} -C ./{images_folder}/')

            logger.debug(f'Moving images into root folder')
            move_images_from_sub_to_root_folder(images_folder, images_folder)
            remove_all_subfolders_inside_folder(images_folder)

            logger.debug(f'Resizing images')
            resize_folder_images(
                src_dir=images_folder, 
                dst_dir='train',
                size=720
            )
            os.system(f'rm -r {images_folder}')
            os.system(f'rm {images_file_name}')
            os.system(f'rm {images_md5_file_name}')
        else:
            logger.error(f'{images_file_name} was not processed due to md5 missmatch')
    except:
        logger.error(f'FAILED TO PROCESS {images_file_name}')
        
if __name__ == "__main__":
    
    logger = create_logger('download.log')
    indexes = []
    with open(sys.argv[1]) as f:
        for line in f:
            indexes.append(line[:-1] + ".jpg")
    p = Pool(processes=10)
    p.map(download_resize_clean, range(500))
    p.close()