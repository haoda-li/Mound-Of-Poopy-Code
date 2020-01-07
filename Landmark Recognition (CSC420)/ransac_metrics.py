import cv2
import numpy as np

def computeTrials(inNum, total_samples, num_samples, threshRatio):
    ratio = inNum / total_samples
    proportion = 1 - ratio ** num_samples
    if proportion == 0:
        return 1
    if proportion == 1:
        return np.inf
    
    return int(np.log(1-threshRatio) / np.log(proportion))
    


def ransac_affine(loc1, loc2, dist_threshold,
           max_trials=300, threshInNum=np.inf, threshLoss=0, threshRatio=0.99):

    best_mat = None
    best_inliers = None
    bestInNum = 0
    bestResLoss = np.inf
    total_samples = loc1.shape[0]
    
    # take 3 points
    if total_samples < 3:
        return 0
    
    indices = np.random.choice(total_samples, 3, replace=False)

    
    for num_trials in range(max_trials):
        
        # calculate affine transformation
        src = np.array([loc1[idx] for idx in indices]).astype(np.float32)
        dst = np.array([loc2[idx] for idx in indices]).astype(np.float32)
        try:
            warp_mat = cv2.getAffineTransform(src, dst)
        except:
            continue
        
        src3d = np.append(loc1, np.ones((loc1.shape[0], 1)), axis=1)
        warp_dst3d = warp_mat @ src3d.T
        warp_dst = warp_dst3d.T
        
        # compare dst points and targets
        distance = np.linalg.norm(warp_dst - loc2, axis=1)
        inliers = distance < dist_threshold
        resLoss = np.sum(distance ** 2)
        inNum = np.count_nonzero(inliers)
        
        new_max_trials = computeTrials(inNum, total_samples, 3, threshRatio)
        
        # update transformation matrix
        if (inNum > bestInNum or (inNum == bestInNum and resLoss < bestResLoss)):
            best_mat = warp_mat
            bestInNum = inNum
            bestResLoss = resLoss
            best_inliers = inliers

        if (bestInNum > threshInNum or bestResLoss < threshLoss or num_trials > new_max_trials):
            break
        
        # resample
        indices = np.random.choice(total_samples, 3, replace=False)

    return bestInNum




def ransac_perspective(loc1, loc2, dist_threshold,
           max_trials=1000, threshInNum=np.inf, threshLoss=0, threshRatio=0.99):

    best_mat = None
    best_inliers = None
    bestInNum = 0
    bestResLoss = np.inf
    total_samples = loc1.shape[0]
    
    # take 3 points
    indices = np.random.choice(total_samples, 4, replace=False)

    
    for num_trials in range(max_trials):
        
        # calculate perspective transformation
        src = np.array([loc1[idx] for idx in indices]).astype(np.float32)
        dst = np.array([loc2[idx] for idx in indices]).astype(np.float32)
        try:
            warp_mat = cv2.getPerspectiveTransform(src, dst)
        except:
            continue
        
        src3d = np.append(loc1, np.ones((loc1.shape[0], 1)), axis=1)
        warp_dst3d = warp_mat @ src3d.T
        warp_dst3d[0] = np.divide(warp_dst3d[0], warp_dst3d[2])
        warp_dst3d[1] = np.divide(warp_dst3d[1], warp_dst3d[2])
        warp_dst = warp_dst3d.T[:, 0:2]
        
        # compare dst points and targets
        distance = np.linalg.norm(warp_dst - loc2, axis=1)
        inliers = distance < dist_threshold
        resLoss = np.sum(distance ** 2)
        inNum = np.count_nonzero(inliers)
        
        new_max_trials = computeTrials(inNum, total_samples, 4, threshRatio)
        
        # update transformation matrix
        if (inNum > bestInNum or (inNum == bestInNum and resLoss < bestResLoss)):
            best_mat = warp_mat
            bestInNum = inNum
            bestResLoss = resLoss
            best_inliers = inliers

        if (bestInNum > threshInNum or bestResLoss < threshLoss or num_trials > new_max_trials):
            break
        
        # resample
        indices = np.random.choice(total_samples, 4, replace=False)

    return best_mat, bestInNum/len(loc1)


def ransac_homography(loc1, loc2, dist_threshold,
           max_trials=1000, threshInNum=np.inf, threshLoss=0, threshRatio=0.99):

    best_mat = None
    best_inliers = None
    bestInNum = 0
    bestResLoss = np.inf
    total_samples = loc1.shape[0]
    
    # take 3 points
    indices = np.random.choice(total_samples, 8, replace=False)

    
    for num_trials in range(max_trials):
        
        # calculate affine transformation
        src = np.array([loc1[idx] for idx in indices]).astype(np.float32)
        dst = np.array([loc2[idx] for idx in indices]).astype(np.float32)
        try:
            warp_mat = cv2.findHomography(src, dst)[0]
        except:
            continue
        
        src3d = np.append(loc1, np.ones((loc1.shape[0], 1)), axis=1)
        warp_dst3d = warp_mat @ src3d.T
        warp_dst3d[0] = np.divide(warp_dst3d[0], warp_dst3d[2])
        warp_dst3d[1] = np.divide(warp_dst3d[1], warp_dst3d[2])
        warp_dst = warp_dst3d.T[:, 0:2]
        
        # compare dst points and targets
        distance = np.linalg.norm(warp_dst - loc2, axis=1)
        inliers = distance < dist_threshold
        resLoss = np.sum(distance ** 2)
        inNum = np.count_nonzero(inliers)
        
        new_max_trials = computeTrials(inNum, total_samples, 8, threshRatio)
        
        # update transformation matrix
        if (inNum > bestInNum or (inNum == bestInNum and resLoss < bestResLoss)):
            best_mat = warp_mat
            bestInNum = inNum
            bestResLoss = resLoss
            best_inliers = inliers

        if (bestInNum > threshInNum or bestResLoss < threshLoss or num_trials > new_max_trials):
            break
        
        # resample
        indices = np.random.choice(total_samples, 8, replace=False)

    return best_mat, bestInNum/len(loc1)
