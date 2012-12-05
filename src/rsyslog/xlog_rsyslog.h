
#ifndef __XLOG_RSYSLOG_h__
#define __XLOG_RSYSLOG_H__

#ifdef  __cplusplus
extern "C" {
#endif
 

int init_xlog(char* const catStr);
int send_to_xlog(char* const msg, int const len );

int close_xlog();
#ifdef  __cplusplus
}
#endif
 

#endif
