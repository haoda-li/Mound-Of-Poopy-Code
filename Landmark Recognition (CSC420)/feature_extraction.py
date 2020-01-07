import cv2
import numpy as np

def computeMatches_SIFT(image1, image2):
    ratio_thresh = 0.7
    
    sift = cv2.xfeatures2d.SIFT_create()
    kp1, des1 = sift.detectAndCompute(image1,None)
    kp2, des2 = sift.detectAndCompute(image2,None)
    for row in range(des1.shape[0]):
        des1[row] = des1[row]/np.linalg.norm(des1[row])
    for row in range(des2.shape[0]):
        des2[row] = des2[row]/np.linalg.norm(des2[row])
    
    bf = cv2.BFMatcher()
    bf_matches = bf.knnMatch(des1,des2,k=2)
    
    matches = []
    for m,n in bf_matches:
        if m.distance < ratio_thresh * n.distance:
            matches.append(m)
    
    return kp1, des1, kp2, des2, matches



def computeMatches_SURF(image1, image2):
    minHessian = 500
    ratio_thresh = 0.7

    surf = cv2.xfeatures2d_SURF.create(hessianThreshold=minHessian)
    kp1, des1 = surf.detectAndCompute(image1, None)
    kp2, des2 = surf.detectAndCompute(image2, None)
    for row in range(des1.shape[0]):
        des1[row] = des1[row]/np.linalg.norm(des1[row])
    for row in range(des2.shape[0]):
        des2[row] = des2[row]/np.linalg.norm(des2[row])

    fl_matcher = cv2.DescriptorMatcher_create(cv2.DescriptorMatcher_FLANNBASED)
    knn_matches = fl_matcher.knnMatch(des1, des2, 2)

    matches = []
    for m,n in knn_matches:
        if m.distance < ratio_thresh * n.distance:
            matches.append(m)
    
    return kp1, des1, kp2, des2, matches