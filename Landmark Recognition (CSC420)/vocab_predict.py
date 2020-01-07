import cv2
import numpy as np
import os
import sys
import argparse
import pickle

from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
from sklearn.externals import joblib


# For a query image, lookup all the visual words in the inverted file index to
# get a list of images that share at least one visual word with the query
def get_candidates(query_image):
    surf = cv2.xfeatures2d.SURF_create()
    kp, des = surf.detectAndCompute(query_image, None)
    if perform_pca:
        pred = kmeans.predict(pca.transform(des))
    else:
        pred = kmeans.predict(des)
    query_features = np.zeros((1, num_clusters))

    for k in range(len(pred)):
        query_features[0][pred[k]] += 1

    candidates = []
    for i in range(num_clusters):
        if (query_features[0][i] > 2):
            candidates.extend(inverted_file_index[i])

    candidates = list(set(candidates))
    return query_features, candidates


# Compute similarity between query BoW vector and all retrieved image BoW
# vectors. Sort (highest to lowest). Take top K most similar images
def getTopCandidates(top_k, query_features, candidates):
    queryIdf = idf * query_features
    queryIdf /= (np.linalg.norm(queryIdf) + 1e-7)

    # take candidate features
    candidate_features = weighted_features[candidates]

    similarity = queryIdf.reshape(1, -1) @ candidate_features.T
    # get top k (large to small)
    indices = np.argsort(-similarity)[0:top_k]
    return np.array([indices[0][0:top_k]])


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--image_path", type=str)
    parser.add_argument("--top_k", type=int, default=1)
    args = parser.parse_args()

    image_path = args.image_path
    top_k = args.top_k

    kmeans = joblib.load('./vocab_model/kmeans_model.joblib')
    pca = joblib.load('./vocab_model/pca_model.joblib')

    with open('./vocab_model/vocab_tree_model.pkl', 'rb') as input_file:
        model = pickle.load(input_file)

    num_clusters = model['num_clusters']
    perform_pca = model['perform_pca']
    idf = model['idf']
    weighted_features = model['weighted_features']
    inverted_file_index = model['inverted_file_index']

    query_image = cv2.imread(image_path)
    # candidates: image index of candidates
    query_features, candidates = get_candidates(query_image)
    # image index of top k candidates
    top_k_candidates = getTopCandidates(top_k, query_features, candidates)[0]
    print(top_k_candidates)