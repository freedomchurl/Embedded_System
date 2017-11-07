//
// Created by caucse on 2017-11-01.
//

#include<jni.h>
#include<stdio.h>
#include<unistd.h>
#include<fcntl.h>
#include<sys/ioctl.h>
#include<android/log.h>

typedef struct {
    unsigned char data[6];
}ioctl_hseg_data;

#define SSEG_MAGIC      0xBD
#define SSEG_SET_HSEG   _IOW(SSEG_MAGIC,0,ioctl_hseg_data)

JNIEXPORT   jint    JNICALL
Java_com_example_caucse_a7segment_18_MainActivity_SSegmentWrite(JNIEnv * jenv,jobject self, jint data)
{
    int dev;


    if((dev = open("/dev/7segment",O_WRONLY | O_SYNC)) < 0)
    {
        __android_log_print(ANDROID_LOG_ERROR,"SSegment","failed to open /dev/7segment\n");
        return 1;
    }


    write(dev,&data,sizeof(int));
    close(dev);

    return 0;

}

JNIEXPORT jint JNICALL
Java_com_example_caucse_a7segment_18_MainActivity_SSegmentIOCtlHseg(JNIEnv * jenv,jobject self, jbyteArray arr)
{
    int dev, len;
    jbyte *hseg;

    if((dev = open("/dev/7segment",O_WRONLY | O_SYNC)) < 0)
    {
        __android_log_print(ANDROID_LOG_ERROR,"SSegment","failed to open /dev/7segment\n");
        return 1;
    }

    if((len = (*jenv)->GetArrayLength(jenv,arr) !=6))
    {
        __android_log_print(ANDROID_LOG_ERROR,"SSegment","invalid length of param arr\n");
        return 1;
    }

    hseg = (*jenv) -> GetByteArrayElements(jenv,arr,NULL);
    ioctl(dev,SSEG_SET_HSEG,hseg,_IOC_SIZE(SSEG_SET_HSEG));
    (*jenv)->ReleaseByteArrayElements(jenv,arr,hseg,0);
    close(dev);

    return 0;

}