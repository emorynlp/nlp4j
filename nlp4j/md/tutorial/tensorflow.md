# Install

* Install [python 3.x](https://www.python.org).
* Install [pip](https://pip.pypa.io):

  ```bash
  # https://bootstrap.pypa.io/get-pip.py
  python3 get-pip.py
  ```

* Install [virtualenv](https://pypi.python.org/pypi/virtualenv):

  ```bash
  sudo pip3 install --upgrade virtualenv  
  ```


* Create a virtual environment in the directory, `vnlp`:

  ```bash
  virtualenv --system-site-packages vnlp
  ```
  
* Activate the `vnlp` environment:

  ```bash
  source vnlp/bin/activate
  ```
  
* Install [tensorflow](https://www.tensorflow.org) under `vnlp`:

  ```bash
  # linux
  pip3 install --upgrade https://storage.googleapis.com/tensorflow/linux/cpu/tensorflow-0.8.0-cp27-none-linux_x86_64.whl
  # mac
  pip3 install --upgrade https://storage.googleapis.com/tensorflow/mac/tensorflow-0.8.0-py3-none-any.whl
  ```

* Install [scipy](pip3 install scipy) and [scikit-learn](http://scikit-learn.org) under `vnlp`:

  ```
  pip3 install -U scipy
  pip3 install -U scikit-learn
  ```