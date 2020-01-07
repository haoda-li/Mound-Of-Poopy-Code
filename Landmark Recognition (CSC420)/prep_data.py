import pandas as pd
import sys
import os

def prep_data_index_file(file_name, train_file, valid_file, test_file, data_index): 
    """ Load the original index csv file from Google's landmark dataset v2
        and samples from the most famous 1000 classes, and make a 7:2:1
        split to train, valid, test data
    """
    index = pd.read_csv(file_name)
    landmark = index.groupby("landmark_id").count()
    
    most_famous = landmark.sort_values("id", ascending=False).iloc[: 1000]
    most_famous = most_famous.reset_index()['landmark_id']
    selected = pd.DataFrame()
    test = pd.DataFrame()
    for lid in most_famous:
        find = index.loc[index.landmark_id == lid]
        train_pool = find.sample(min(200, len(find)))
        test = test.append(find.drop(train_pool.index))
        selected = selected.append(train_pool)
    test = test.sample(250)
    selected = selected[['id', 'landmark_id']]
    test = test[['id', 'landmark_id']]
    land_map = selected.groupby("landmark_id").first().reset_index()[['landmark_id']]
    land_map['class_id'] = land_map.index
    selected = selected.merge(land_map)
    test = test.merge(land_map)
    with open(data_index, "w") as f:
        for row in selected.iterrows():
            f.write(row[1]['id'] + "\n")
        for row in test.iterrows():
            f.write(row[1]['id'] + "\n")
    
    train = selected.sample(frac=0.8)
    valid = selected.drop(train.index)
    
    train.to_csv(train_file, index=False)
    valid.to_csv(valid_file, index=False)
    test.to_csv(test_file, index=False)
    

def adding_noise_to_test(test_file, noise_folder):
    """ Add the non-landmark images into the test dataset
    """
    test = pd.read_csv(test_file)
    noise = {
        'id': [],
        'landmark_id': [],
        'class_id': []
    }
    for filename in os.listdir(noise_folder):
        noise['id'].append(filename.replace(".jpg", ""))
        noise['landmark_id'].append(1000)
        noise['class_id'].append(1000)
    test = test.append(pd.DataFrame(noise))
    test.to_csv(test_file, index=False)
    
def prepare_class_sample(train_file, class_sample):
    train = pd.read_csv(train_file)
    ret = pd.DataFrame()
    for i in range(1000):
        ret = ret.append(train.loc[train.class_id == i].sample(20))
    ret.to_csv(class_sample, index=False)
        
if __name__ == "__main__":
    file_name, train_file, valid_file, test_file, data_index, noise_folder, class_sample = sys.argv[1:]
    # prep_data_index_file(file_name, train_file, valid_file, test_file, data_index)
    # adding_noise_to_test(test_file, noise_folder)
    prepare_class_sample(train_file, class_sample)
    
