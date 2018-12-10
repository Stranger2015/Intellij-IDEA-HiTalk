#!/bin/bash

pushd hxcpp-debugger-protocol/src
logtalk -lib debugger -java JavaProtocol -main JavaProtocol
popd
