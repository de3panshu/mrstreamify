package com.hd.api.mrstreamify.exception;

public class ChunkAlreadyUploadedException extends RuntimeException{
    public ChunkAlreadyUploadedException(int chunkIndex,String videoId){
        super(String.format("Chunk-%d is already uploaded for video-id(%s), Try sending rest of the chunks.", chunkIndex, videoId));
    }
}
