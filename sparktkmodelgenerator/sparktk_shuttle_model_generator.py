
# coding: utf-8

# In[ ]:

#
# Copyright (c) 2016 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#



# ## importing the sparktk and tap_catalog libraries give you the capability of creating machine learning models, performing data wrangling, and publishing of the model to the data catalog.

# In[ ]:

import sparktk
import tap_catalog
from sparktk import TkContext
from tap_catalog import DataCatalog

print "SparkTK installation path = %s" % (sparktk.__path__)

tc = TkContext()


# ## Reading in the data to train the model
# ## You must change the hdfs path to the path of the datafile

# In[ ]:


ds = "hdfs://nameservice1/org/29ace093-e11f-4f0b-b254-3f8e973476e5/brokers/userspace/694b3da9-c21a-4063-bf16-e072ac47f881/30fc50da-f065-41d8-a510-77d0b7683a47/000000_1"
sc = [("label", float), ("feature1", float), ("feature2", float), ("feature3", float), ("feature4", float),
      ("feature5", float), ("feature6", float), ("feature7", float), ("feature8", float), ("feature9", float)]

frame = tc.frame.import_csv(ds,schema=sc)

frame.inspect()


# ## Creation and training of the model can happen in one step

# In[ ]:


m = tc.models.classification.svm.train(frame,
                                       ["feature1", "feature2", "feature3", "feature4", "feature5", "feature6", "feature7", "feature8", "feature9"],
                                       label_column='label')



# ## Export the model to MAR format, enabled the scoring engine to use it

# In[ ]:

m.export_to_mar("hdfs://nameservice1/user/vcap/spaceshuttleSVMmodel.mar")


# ## The next statement will ask you the URL, User Name and password.  It is used by OAuth to authenticate the user with the TAP instance.

# In[ ]:

data_catalog = DataCatalog()


# ## The next statement added the Trained Model to the TAP data catalog.

# In[ ]:

data_catalog.add("hdfs://nameservice1/user/vcap/spaceshuttleSVMmodel.mar")


# ## "hdfsclient" gives you the capability of looking at the properties of the models created, and deletion of those models

# In[ ]:

import hdfsclient


# In[ ]:

hdfsclient.ls('/user/vcap/*.mar')


# In[ ]:

#hdfsclient.rm('spaceshuttleSVMmodel.mar')


# In[ ]:
