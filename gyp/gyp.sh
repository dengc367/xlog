#!/bin/bash

export LD_LIBRARY_PATH=$LB_LIBRARY_PATH:/opt/Ice-3.3/lib/
gyp --depth=. xlog.gyp -Dlibrary=static_library 
