{
    'includes': [
        'xlog_vars.gyp',
    ],
    'variables': {
        'GEN_PATH':'<(ROOT)/build/generated',

    },
    'targets': [
        {
            'target_name':'xlog_generated',
            'type':'none',
            'direct_dependent_settings': {
                'target_conditions': [
                    ['_type=="executable"', {
                        'conditions':[
                            ['OS=="linux"',{
                                'libraries': [
                                    '-L<(PROTOBUF_HOME)/lib', 
                                '-lprotobuf',
                                ], 
                            }],
                        ],
                    }],
                ],
            },
            'sources': [
                '<(ROOT)/interface/xlog.proto',
                '<(ROOT)/interface/xlog.ice',

            ],
            'rules': [
            {
                'rule_name':'genproto',
                'extension':'proto',
                'inputs': [],
                'variables': {
                    'rule_input_relpath':'<(ROOT)/interface',
                    'PROTOC_BIN': '<(PROTOBUF_HOME)/bin/protoc',
                    'PROTOC_GEN_PATH': '<(GEN_PATH)',
                },
                'outputs': [
                    '<(PROTOC_GEN_PATH)/<(RULE_INPUT_ROOT).pb.h',
                    '<(PROTOC_GEN_PATH)/<(RULE_INPUT_ROOT).pb.cc',
                ],
                'action': [
                    '<(PROTOC_BIN)',
                    '--proto_path=<(rule_input_relpath)',
                    '<(rule_input_relpath)/<(RULE_INPUT_NAME)',
                    '--cpp_out=<(PROTOC_GEN_PATH)',
                ],
                'message':'Generating C++ code from <(RULE_INPUT_PATH)',
            },
            {
                'rule_name':'genslice',
                'extension':'ice',
                'inputs': [],
                'variables': {
                    'rule_input_relpath':'<(ROOT)/interface',
                    'SLICE2CPP_BIN': '<(ICE_HOME)/bin/slice2cpp',
                    'SLICE_GEN_PATH': '<(GEN_PATH)',
                },
                'outputs': [
                    '<(SLICE_GEN_PATH)/<(RULE_INPUT_ROOT).h',
                    '<(SLICE_GEN_PATH)/<(RULE_INPUT_ROOT).cpp',
                ],
                'action': [
                    '<(SLICE2CPP_BIN)',
                    '-I<(ICE_SLICE_HOME)/slice',
                    '<(RULE_INPUT_PATH)',
                    '--output-dir=<(SLICE_GEN_PATH)',
                ],
                'message':'Generating C++ code from <(RULE_INPUT_PATH)',
            },
            ],
        },


        #### config.bootstrap
        {
            'target_name': 'config.bootstrap',
            'type': 'executable',
            'defines': [
                'OS_LINUX',
                'POSIX',
                'NEWARCH',
            ],
            'include_dirs': [
                '<(ROOT)',
            ],
            'dependencies': [
                'xlog_generated', 
            ],
            'sources': [
                '<(ROOT)/build/generated/xlog.cpp',
                '<(ROOT)/build/generated/xlog.pb.cc',
               # '<(ROOT)/src/config/dispatcher_config.cpp',
                '<(ROOT)/src/config/client_config_manager.cpp',
                '<(ROOT)/src/config/config_bootstrap.cpp',
                '<(ROOT)/src/common/zk_manager.cpp',
                '<(ROOT)/src/common/zk_conn.cpp',
            ],
            'conditions': [
                ['OS=="linux"', {
                    'include_dirs': [
                        '<(ZOOKEEPER_HOME)/include', 
                        '<(PROTOBUF_HOME)/include', 
                        '<(ICE_HOME)/include', 
                        '<(GEN_PATH)',
                    ],
                    'libraries': [
                        '-L<(ICE_HOME)/lib', 
                        '-lIce', 
                        '-lIceUtil', 
                        '-L<(ZOOKEEPER_HOME)/lib', 
                        '-lzookeeper_mt', 
                        '-L<(PROTOBUF_HOME)/lib', 
                        '-lprotobuf', 
                    ]
                }],
            ],
        },

        #### xlog agent target
        {
            'target_name': 'agent.binary',
            'type': 'executable',
            'defines': [
                'OS_LINUX',
                'POSIX',
                'NEWARCH',
            ],
            'include_dirs': [
                '<(ROOT)',
            ],
            'dependencies': [
                'xlog_generated', 
            ],
            'sources': [
                '<(ROOT)/build/generated/xlog.cpp',
                '<(ROOT)/build/generated/xlog.pb.cc',
                '<(ROOT)/src/adapter/dispatcher_adapter.cpp',
                '<(ROOT)/src/config/dispatcher_config.cpp',
                '<(ROOT)/src/config/client_config_manager.cpp',
                '<(ROOT)/src/config/agent_config_manager.cpp',
                '<(ROOT)/src/agent/AgentI.cpp',
                '<(ROOT)/src/agent/agent.cpp',
                '<(ROOT)/src/common/zk_manager.cpp',
                '<(ROOT)/src/common/zk_conn.cpp',
                '<(ROOT)/src/adapter/client_adapter.cpp',
            ],
            'conditions': [
                ['OS=="linux"', {
                    'include_dirs': [
                        '<(ZOOKEEPER_HOME)/include', 
                        '<(PROTOBUF_HOME)/include', 
                        '<(LOG4CPLUS_HOME)/include', 
                        '<(ICE_HOME)/include', 
                        '<(GEN_PATH)',
                    ],
                    'libraries': [
                        '-L<(ICE_HOME)/lib', 
                        '-lIce', 
                        '-lIceUtil', 
                        '-L<(ZOOKEEPER_HOME)/lib', 
                        '-lzookeeper_mt', 
                        '-L<(PROTOBUF_HOME)/lib', 
                        '-lprotobuf', 
                        '-L<(LOG4CPLUS_HOME)/lib', 
                        '-llog4cplus', 
                    ]
                }],
            ],
        },

        ##### xlog client shared library
        {
            'target_name': 'XLogClient',
            'type': 'static_library',
            'msvs_guid': '5ECEC9E5-8F23-47B6-93E0-C3B328B3BE65',
            'defines': [
                'OS_LINUX',
                'POSIX',
                'NEWARCH',
            ],
            'include_dirs': [
                '<(ROOT)',
            ],
            'dependencies': [
                'xlog_generated', 
            ],
            'sources': [
                #'<(ROOT)/src/example/c++/xlog_appender_test.cpp',
                '<(ROOT)/src/example/c++/xlog_appender_test_for_multi_thread.cpp',
                '<(ROOT)/src/client/xlog_appender.cpp',
                '<(ROOT)/src/client/xlog_properties.cpp',
                '<(ROOT)/src/client/client.cpp',
                '<(ROOT)/build/generated/xlog.cpp',
                '<(ROOT)/src/adapter/agent_adapter.cpp',
            ],
            'conditions': [
                ['OS=="linux"', {
                    'include_dirs': [
                        '<(ZOOKEEPER_HOME)/include', 
                        '<(PROTOBUF_HOME)/include', 
                        '<(RSYSLOG_HOME)/runtime', 
                        '<(LOG4CPLUS_HOME)/include', 
                        '<(RSYSLOG_HOME)/', 
                        '<(ICE_HOME)/include', 
                        '<(GEN_PATH)',
                    ],
                    'libraries': [
                        '-L<(ICE_HOME)/lib', 
                        '-lIce', 
                        '-lIceUtil', 
                        '-L<(LOG4CPLUS_HOME)/lib', 
                        '-llog4cplus', 
                    ]
                }],
            ],
        },

        ##### xlog client target
        {
            'target_name': 'xlog_client',
            'type': 'executable',
            'msvs_guid': '5ECEC9E5-8F23-47B6-93E0-C3B328B3BE65',
            'defines': [
                'OS_LINUX',
                'POSIX',
                'NEWARCH',
            ],
            'include_dirs': [
                '<(ROOT)',
            ],
            'dependencies': [
                'xlog_generated', 
                'XLogClient', 
            ],
            'sources': [
                #'<(ROOT)/src/example/c++/xlog_appender_test.cpp',
                '<(ROOT)/src/example/c++/xlog_appender_test_for_multi_thread.cpp',
                #'<(ROOT)/src/client/xlog_appender.cpp',
                #'<(ROOT)/src/client/xlog_properties.cpp',
                #'<(ROOT)/src/client/client.cpp',
                #'<(ROOT)/build/generated/xlog.cpp',
                #'<(ROOT)/src/adapter/agent_adapter.cpp',
            ],
            'conditions': [
                ['OS=="linux"', {
                    'include_dirs': [
                        '<(ZOOKEEPER_HOME)/include', 
                        '<(PROTOBUF_HOME)/include', 
                        '<(RSYSLOG_HOME)/runtime', 
                        '<(LOG4CPLUS_HOME)/include', 
                        '<(RSYSLOG_HOME)/', 
                        '<(ICE_HOME)/include', 
                        '<(GEN_PATH)',
                    ],
                    'libraries': [
                        '-L<(ICE_HOME)/lib', 
                        '-lIce', 
                        '-lIceUtil', 
                        '-L<(LOG4CPLUS_HOME)/lib', 
                        '-llog4cplus', 
                    ]
                }],
            ],
        },

        ##### xlog stdin target
        {
            'target_name': 'xlog_cli',
            'type': 'executable',
            'msvs_guid': '5ECEC9E5-8F23-47B6-93E0-C3B328B3BE65',
            'defines': [
                'OS_LINUX',
                'POSIX',
                'NEWARCH',
            ],
            'include_dirs': [
                '<(ROOT)',
            ],
            'dependencies': [
                'xlog_generated', 
            ],
            'sources': [
                '<(ROOT)/src/client/xlog_stdin.cpp',
                '<(ROOT)/src/client/xlog_appender.cpp',
                '<(ROOT)/src/client/xlog_properties.cpp',
                '<(ROOT)/src/client/client.cpp',
                '<(ROOT)/build/generated/xlog.cpp',
                '<(ROOT)/src/adapter/agent_adapter.cpp',
            ],
            'conditions': [
                ['OS=="linux"', {
                    'include_dirs': [
                        '<(ZOOKEEPER_HOME)/include', 
                        '<(PROTOBUF_HOME)/include', 
                        '<(ICE_HOME)/include', 
                        '<(GEN_PATH)',
                    ],
                    'libraries': [
                        '-L<(ICE_HOME)/lib', 
                        '-lIce', 
                        '-lIceUtil', 
                    ]
                }],
            ],
        },

        ##### xlog rsyslog target
        {
            'target_name': 'xlog_rsyslog',
            'type': 'shared_library',
            'msvs_guid': '5ECEC9E5-8F23-47B6-93E0-C3B328B3BE65',
            'defines': [
                'OS_LINUX',
                'POSIX',
                'NEWARCH',
            ],
            'include_dirs': [
                '<(ROOT)',
            ],
            'dependencies': [
                'xlog_generated', 
            ],
            'sources': [
                '<(ROOT)/src/rsyslog/omxlog.c',
                '<(ROOT)/src/rsyslog/xlog_rsyslog.cpp',
                '<(ROOT)/src/client/xlog_appender.cpp',
                '<(ROOT)/src/client/xlog_properties.cpp',
                '<(ROOT)/src/client/client.cpp',
                '<(ROOT)/build/generated/xlog.cpp',
                '<(ROOT)/src/adapter/agent_adapter.cpp',
            ],
            'conditions': [
                ['OS=="linux"', {
                    'include_dirs': [
                        '<(ZOOKEEPER_HOME)/include', 
                        '<(PROTOBUF_HOME)/include', 
                        '<(RSYSLOG_HOME)/runtime', 
                        '<(RSYSLOG_HOME)/', 
                        '<(ICE_HOME)/include', 
                        '<(GEN_PATH)',
                    ],
                    'libraries': [
                        '-L<(ICE_HOME)/lib', 
                        '-lIce', 
                        '-lIceUtil', 
                    ]
                }],
            ],
        },

        ##### xlog test target
        {
            'target_name': 'xlog_test',
            'type': 'executable',
            'defines': [
                'OS_LINUX',
                'POSIX',
                'NEWARCH',
            ],
            'include_dirs': [
                '<(ROOT)',
            ],
            'dependencies': [
                'xlog_generated', 
            ],
            'sources': [
                '<(ROOT)/src/common/logger.cpp',
            ],
            'conditions': [
                ['OS=="linux"', {
                    'include_dirs': [
                        '<(ZOOKEEPER_HOME)/include', 
                        '<(PROTOBUF_HOME)/include', 
                        '<(RSYSLOG_HOME)/runtime', 
                        '<(RSYSLOG_HOME)/', 
                        '<(ICE_HOME)/include', 
                        '<(LOG4CPLUS_HOME)/include', 
                        '<(GEN_PATH)',
                    ],
                    'libraries': [
                        '-L<(ICE_HOME)/lib', 
                        '-lIce', 
                        '-lIceUtil', 
                        '-L<(LOG4CPLUS_HOME)/lib', 
                        '-llog4cplus', 
                    ]
                }],
            ],
        },
    ],
}
