package com.lndf.glengine.model;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIFileCloseProc;
import org.lwjgl.assimp.AIFileFlushProc;
import org.lwjgl.assimp.AIFileIO;
import org.lwjgl.assimp.AIFileOpenProc;
import org.lwjgl.assimp.AIFileReadProc;
import org.lwjgl.assimp.AIFileSeek;
import org.lwjgl.assimp.AIFileTellProc;
import org.lwjgl.assimp.AIFileWriteProc;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.asset.Asset;

public class ModelImporter {

	private static PointerBuffer getAIFileMeta(long pFile) { return memPointerBuffer(memGetAddress(pFile + AIFile.USERDATA), 3);}
	 
	private static final AIFileReadProc  AI_FILE_READ_PROC  = AIFileReadProc.create((pFile, pBuffer, size, count) -> {
		PointerBuffer meta = getAIFileMeta(pFile);
	 
		long position = meta.get(1);
	 
		long remaining = meta.get(2) - position;
		long requested = size * count;
	 
		long elements = Long.compareUnsigned(requested, remaining) <= 0
			? count
			: Long.divideUnsigned(remaining, size);
	 
		memCopy(meta.get(0) + position, pBuffer, size * elements);
		meta.put(1, position + size * elements);
	 
		return elements;
	});
	
	private static final AIFileWriteProc AI_FILE_WRITE_PROC = AIFileWriteProc.create((pFile, pBuffer, memB, count) -> {
		throw new UnsupportedOperationException();
	});
	
	private static final AIFileTellProc  AI_FILE_TELL_PROC  = AIFileTellProc.create(pFile -> getAIFileMeta(pFile).get(1));
	
	private static final AIFileTellProc  AI_FILE_SIZE_PROC  = AIFileTellProc.create(pFile -> getAIFileMeta(pFile).get(2));
	
	private static final AIFileSeek	  AI_FILE_SEEK_PROC  = AIFileSeek.create((pFile, offset, origin) -> {
		PointerBuffer meta = getAIFileMeta(pFile);
	 
		long limit = meta.get(2);
	 
		long position;
		switch (origin) {
			case aiOrigin_SET:
				position = offset;
				break;
			case aiOrigin_CUR:
				position = meta.get(1) + offset;
				break;
			case aiOrigin_END:
				position = limit - offset;
				break;
			default:
				throw new IllegalArgumentException();
		}
	 
		if (position < 0 || limit < position) {
			return -1;
		}
		meta.put(1, position);
		return 0;
	});
	
	private static final AIFileFlushProc AI_FILE_FLUSH_PROC = AIFileFlushProc.create(pFile -> { throw new UnsupportedOperationException(); });
	 
	private static final AIFileOpenProc  AI_FILE_OPEN_PROC  = AIFileOpenProc.create((pFileIO, fileName, openMode) -> {
		ByteBuffer data = null;
		String strFileName = memUTF8(fileName);
		try {
			data = new Asset(strFileName).getByteBuffer();
		} catch (IOException e) {
			System.out.println("ASSIMPLOADER: Couldn't load asset " + strFileName);
			return 0;
		}
	 
		MemoryStack stack = stackGet();
		return AIFile.callocStack(stack)
			.ReadProc(AI_FILE_READ_PROC)
			.WriteProc(AI_FILE_WRITE_PROC)
			.TellProc(AI_FILE_TELL_PROC)
			.FileSizeProc(AI_FILE_SIZE_PROC)
			.SeekProc(AI_FILE_SEEK_PROC)
			.FlushProc(AI_FILE_FLUSH_PROC)
			// metadata about the file buffer
			.UserData(stack.mallocPointer(3)
				.put(0, memAddress(data)) // origin
				.put(1, 0L) // current position
				.put(2, data.remaining()) // capacity
				.address())
			.address();
	});
	
	private static final AIFileCloseProc AI_FILE_CLOSE_PROC = AIFileCloseProc.create((pFileIO, pFile) -> {});
	
	public static AIScene importScene(Asset asset, int flags) {
		try (MemoryStack stack = stackPush()) {
	        return aiImportFileEx(
	            asset.toString(),
	            flags,
	            AIFileIO.callocStack(stack)
	                .OpenProc(ModelImporter.AI_FILE_OPEN_PROC)
	                .CloseProc(ModelImporter.AI_FILE_CLOSE_PROC)
	                .UserData(-1L)
	        );
	    }
	}
}
