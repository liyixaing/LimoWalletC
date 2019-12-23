cmake_minimum_required(VERSION 3.4.1)

include_directories(${CMAKE_SOURCE_DIR}/include)
link_directories(${CMAKE_SOURCE_DIR}/libs)

add_library(
        jgit2
        SHARED
        #source codes
        ${CMAKE_SOURCE_DIR}/jni/LMRepo.cpp
        ${CMAKE_SOURCE_DIR}/jni/strconv.cpp
)

#find_library( StaticSSH2 ${CMAKE_SOURCE_DIR}/std.libs/libssh2.a )

#find_library( log-lib log )
#find_library( git2-lib ${CMAKE_SOURCE_DIR}/std.libs/libgit2.a )

#link_libraries( ${git2-lib} )
#target_link_libraries( native-lib ${log-lib} )

#target_link_libraries( jgit2 -lgit2 -lssh2 -lgcrypt -lgpg-error -lssl -lcrypto -llog )
target_link_libraries( jgit2 -lgit2 -lssh2 -lgcrypt -lgpg-error -lssl -lcrypto -llog )