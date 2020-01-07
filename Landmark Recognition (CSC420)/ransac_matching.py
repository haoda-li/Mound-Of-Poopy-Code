import cv2
import numpy as np
from ransac_metrics import *


def computeMatches(kp1, des1, kp2, des2):
    
    ratio_thresh = 0.8
    matches = []
    
    try:
        fl_matcher = cv2.DescriptorMatcher_create(cv2.DescriptorMatcher_FLANNBASED)
        knn_matches = fl_matcher.knnMatch(des1, des2, 2)

        
        for m,n in knn_matches:
            if m.distance < ratio_thresh * n.distance:
                matches.append(m)
    except: 
        return []
    return matches


def randsac_matching_metrics(top_class, x_name, df, dir_):
    match_counts = np.zeros(len(top_class))
    
    x = cv2.imread(x_name, cv2.IMREAD_GRAYSCALE)
    minHessian = 450
    
    surf = cv2.xfeatures2d_SURF.create(hessianThreshold=minHessian)
    kp1, des1 = surf.detectAndCompute(x, None)
    for row in range(des1.shape[0]):
        des1[row] = des1[row]/np.linalg.norm(des1[row])
   
    for i_class in range(len(top_class)):
        selected = list(df.loc[df.class_id == top_class[i_class]].sample(20).id)
        for image_path in selected:
            y = cv2.imread("/".join(["index", image_path + ".jpg"]), cv2.IMREAD_GRAYSCALE)
            if y is None:
                continue
            kp2, des2 = surf.detectAndCompute(y, None)
            if des2 is None:
                continue
            for row in range(des2.shape[0]):
                des2[row] = des2[row]/np.linalg.norm(des2[row])

            matches = computeMatches(kp1, des1, kp2, des2)
            num = len(matches)
            loc1 = np.array([ kp1[matches[i].queryIdx].pt for i in range(num)])
            loc2 = np.array([ kp2[matches[i].trainIdx].pt for i in range(num)])
            bestInNum = ransac_affine(loc1, loc2, dist_threshold=4, threshRatio=0.99)
            match_counts[i_class] += bestInNum > 14
    if max(match_counts) < 6:
        return 1000
    return top_class[0] # top_class[match_counts.argmax()]
            
    
def randsac_matching_metrics_thresholding(top_class, x_name, df, dir_, dict_kp, dict_des):
    match_counts = np.zeros(len(top_class))
    
    x = cv2.imread(x_name, cv2.IMREAD_GRAYSCALE)
    minHessian = 450
    
    surf = cv2.xfeatures2d_SURF.create(hessianThreshold=minHessian)
    kp1, des1 = surf.detectAndCompute(x, None)
    for row in range(des1.shape[0]):
        des1[row] = des1[row]/np.linalg.norm(des1[row])
   
    for i_class in range(len(top_class)):
        selected = list(df.loc[df.class_id == top_class[i_class]].id)
        for image_path in selected:
            kp2 = dict_kp[image_path]
            des2 = dict_des.get(image_path)
            if des2 is None:
                continue
            for row in range(des2.shape[0]):
                des2[row] = des2[row]/np.linalg.norm(des2[row])
            matches = computeMatches(kp1, des1, kp2, des2)
            num = len(matches)
            loc1 = np.array([ kp1[matches[i].queryIdx].pt for i in range(num)])
            loc2 = np.array([ kp2[matches[i].trainIdx].pt for i in range(num)])
            bestInNum = ransac_affine(loc1, loc2, dist_threshold=2, threshRatio=0.99)
            match_counts[i_class] += bestInNum > 14
    if max(match_counts) < 6:
        return 1000
    return top_class[0]
        

def prepare_features(df):
    minHessian = 450
    surf = cv2.xfeatures2d_SURF.create(hessianThreshold=minHessian)

    dict_kp = {}
    dict_des = {}
    for row in df.iterrows():
        image = cv2.imread("/".join(["index", row[1].id + ".jpg"]), cv2.IMREAD_GRAYSCALE)
        if image is None:
            continue
        kp, des = surf.detectAndCompute(image, None)
        for r in range(des.shape[0]):
            des[r] = des[r] / np.linalg.norm(des[r])
        dict_kp[row[1].id] = kp[:1000]
        dict_des[row[1].id] = des[:1000]
    return dict_kp. dict_des
    
    
    
