
#### Initializing the data set index file 
Download the trainig index file from Google's landmark dataset v2. https://s3.amazonaws.com/google-landmark/metadata/train.csv  
You can also check their repo for more information (https://github.com/cvdfoundation/google-landmark).

Then, prepare your own non-landmark data, or use our non-landmark data in the test set here (https://drive.google.com/open?id=13FgCRmQJkfzD9PJ8H6ez39Wehqmx-ZNM). Store them in `noise` folder

Run the following code
```
python prep_data.py train.csv our_train.csv our_valid.csv our_test.csv data_index.txt noise sample.csv
```

#### Downloading Required Data

```
python data_download.py data_index.txt
```

Note that the data_download is credited to this notebook https://www.kaggle.com/sermakarevich/download-resize-clean-12-hours-44gb. We did some modification to make it serve our purpose. 

Because Google hashes all the images in 500 tar files, although we only use a portion of it, we still have to download all of them and discard most of them. 


#### Train and inference the model
This corresponds to the first stage, which can only classify landmarks, but don't verify whether it is a landmark. 

Our trained model is found here: (https://drive.google.com/open?id=13FgCRmQJkfzD9PJ8H6ez39Wehqmx-ZNM)

To run inference 
```
python run_model.py inference our_valid.csv train model.pth
```

To run train
```
python run_model.py train our_train.csv train model.pth
```

#### Run the test
This is the overall pipeline. Run `python test.py -h` to see all the details

`thresh` mode refers to the hard threshold (not use feature matching, regard any top prediction <0.45 as non-landmark)  
`ransac` mode refers to the RANSAC feature matching (our final reported method)  
`query` and `query_thresh` mode accepts a path to an image and output its prediction

class_sample refers to `sample.csv` generated, class_folder will be `train`. For `thresh` and `query_thresh` modes, passing a pseudo value and the program will run. 

Since thresh mode only uses models, it can run by just downloading the model file. 

Our own test set is provided (https://drive.google.com/open?id=13FgCRmQJkfzD9PJ8H6ez39Wehqmx-ZNM), while the training set is too large to store. our test set index file is `testset.csv` in github repo.

-------------------------------------------------------------------------------------
#### Bag of Visual Words  

Train: create a folder: vocab_model in the same directory as vocab_train.py (used to store models), then run the following script to construct a bag of visual words.    

       vocab_train.py --train_dir=[path to directory of training images] --num_clusters=[number of clusters for kmeans] 
       --perform_pca=[whether to perform PCA on extracted feature descriptors]
       
Test: run  

      vocab_predict.py --image_path=[path to query image] --top_k=[# of matched candidates to select]  
      
   Will return the index of candidate images in the training directory.
