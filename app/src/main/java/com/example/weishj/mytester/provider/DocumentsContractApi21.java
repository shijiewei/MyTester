/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.weishj.mytester.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;

class DocumentsContractApi21 {
    private static final String TAG = "DocumentFile";

    public static Uri createFile(Context context, Uri self, String mimeType,
            String displayName) {
		Uri uri = null;
    	try {
			uri = DocumentsContract.createDocument(context.getContentResolver(), self, mimeType,
					displayName);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Create file error", e);
		}
    	return uri;
	}

    public static Uri createDirectory(Context context, Uri self, String displayName) {
        return createFile(context, self, DocumentsContract.Document.MIME_TYPE_DIR, displayName);
    }

    public static Uri prepareTreeUri(Uri treeUri) {
        return DocumentsContract.buildDocumentUriUsingTree(treeUri,
                DocumentsContract.getTreeDocumentId(treeUri));
    }

    public static Uri[] listFiles(Context context, Uri self) {
        final ContentResolver resolver = context.getContentResolver();
        final Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(self,
                DocumentsContract.getDocumentId(self));
        final ArrayList<Uri> results = new ArrayList<Uri>();

        Cursor c = null;
        try {
            c = resolver.query(childrenUri, new String[] {
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID }, null, null, null);
            while (c.moveToNext()) {
                final String documentId = c.getString(0);
                final Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(self,
                        documentId);
                results.add(documentUri);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed query: " + e);
        } finally {
            closeQuietly(c);
        }

        return results.toArray(new Uri[results.size()]);
    }

    public static Uri renameTo(Context context, Uri self, String displayName) {
    	Uri uri = null;
		try {
			uri = DocumentsContract.renameDocument(context.getContentResolver(), self, displayName);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Rename file error", e);
		}
		return uri;
	}

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
