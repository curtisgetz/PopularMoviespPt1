package com.curtisgetz.popularmoviesppt1.data;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContentProvider;
import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContract;
import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteDbHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TestContentProvider {


    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() {
        /* Use TaskDbHelper to get access to a writable database */
        FavoriteDbHelper dbHelper = new FavoriteDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, null, null);
    }



    //================================================================================
    // Test ContentProvider Registration
    //================================================================================

    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file. If it fails, you should check the AndroidManifest to see if you've
     * added a <provider/> tag and that you've properly specified the android:authorities attribute.
     */
    @Test
    public void testProviderRegistry() {

        /*
         *
         * We will use the ComponentName for our ContentProvider class to ask the system
         * information about the ContentProvider, specifically, the authority under which it is
         * registered.
         */
        String packageName = mContext.getPackageName();
        String taskProviderClassName = FavoriteContentProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, taskProviderClassName);

        try {

            /*
             * Get a reference to the package manager. The package manager allows us to access
             * information about packages installed on a particular device. In this case, we're
             * going to use it to get some information about our ContentProvider under test.
             */
            PackageManager pm = mContext.getPackageManager();

            /* The ProviderInfo will contain the authority, which is what we want to test */
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = packageName;

            /* Make sure that the registered authority matches the authority from the Contract */
            String incorrectAuthority =
                    "Error: TaskContentProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll =
                    "Error: TaskContentProvider not registered at " + mContext.getPackageName();
            /*
             * This exception is thrown if the ContentProvider hasn't been registered with the
             * manifest at all. If this is the case, you need to double check your
             * AndroidManifest file
             */
            fail(providerNotRegisteredAtAll);
        }
    }




    //================================================================================
    // Test UriMatcher
    //================================================================================


    private static final Uri TEST_FAVORITES = FavoriteContract.FavoriteEntry.CONTENT_URI;
    // Content URI for a single task with id = 1
    private static final Uri TEST_FAVORITES_WITH_ID = TEST_FAVORITES.buildUpon().appendPath("1").build();


    /**
     * This function tests that the UriMatcher returns the correct integer value for
     * each of the Uri types that the ContentProvider can handle. Uncomment this when you are
     * ready to test your UriMatcher.
     */
    @Test
    public void testUriMatcher() {

        /* Create a URI matcher that the TaskContentProvider uses */
        UriMatcher testMatcher = FavoriteContentProvider.buildUriMatcher();

        /* Test that the code returned from our matcher matches the expected TASKS int */
        String tasksUriDoesNotMatch = "Error: The TASKS URI was matched incorrectly.";
        int actualTasksMatchCode = testMatcher.match(TEST_FAVORITES);
        int expectedTasksMatchCode = FavoriteContentProvider.FAVORITES;
        assertEquals(tasksUriDoesNotMatch,
                actualTasksMatchCode,
                expectedTasksMatchCode);

        /* Test that the code returned from our matcher matches the expected TASK_WITH_ID */
        String taskWithIdDoesNotMatch =
                "Error: The TASK_WITH_ID URI was matched incorrectly.";
        int actualTaskWithIdCode = testMatcher.match(TEST_FAVORITES_WITH_ID);
        int expectedTaskWithIdCode = FavoriteContentProvider.FAVORITE_WITH_ID;
        assertEquals(taskWithIdDoesNotMatch,
                actualTaskWithIdCode,
                expectedTaskWithIdCode);
    }




//================================================================================
    // Test Insert
    //================================================================================


    /**
     * Tests inserting a single row of data via a ContentResolver
     */
    @Test
    public void testInsert() {

        /* Create values to insert */
        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,351286 );
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_BG_URL, "/gBmrsugfWpiXRh13Vo3j0WW55qD.jpg");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL, "/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_RATING, 4.5);
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, "2018-1-1");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS, "SUPER GOOD");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, "SUPER MOVIE");



        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);


        Uri uri = contentResolver.insert(FavoriteContract.FavoriteEntry.CONTENT_URI, testTaskValues);


        Uri expectedUri = ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI, 1);

        String insertProviderFailed = "Unable to insert item through Provider";
        assertEquals(insertProviderFailed, uri, expectedUri);

        /*
         * If this fails, it's likely you didn't call notifyChange in your insert method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }




    //================================================================================
    // Test Query (for favorites directory)
    //================================================================================


    /**
     * Inserts data, then tests if a query for the tasks directory returns that data as a Cursor
     */
    @Test
    public void testQuery() {

        /* Get access to a writable database */
        FavoriteDbHelper dbHelper = new FavoriteDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* Create values to insert */
        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,351286 );
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_BG_URL, "/gBmrsugfWpiXRh13Vo3j0WW55qD.jpg");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL, "/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_RATING, 4.5);
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, "2018-1-1");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS, "SUPER GOOD");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, "SUPER MOVIE");

        /* Insert ContentValues into database and get a row ID back */
        long taskRowId = database.insert(
                /* Table to insert values into */
                FavoriteContract.FavoriteEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testTaskValues);

        String insertFailed = "Unable to insert directly into the database";
        assertTrue(insertFailed, taskRowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        /* Perform the ContentProvider query */
        Cursor taskCursor = mContext.getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);


        String queryFailed = "Query failed to return a valid Cursor";
        assertTrue(queryFailed, taskCursor != null);

        /* We are done with the cursor, close it now. */
        taskCursor.close();
    }



//================================================================================
    // Test Delete (for a single item)
    //================================================================================


    /**
     * Tests deleting a single row of data via a ContentResolver
     */
    @Test
    public void testDelete() {
        /* Access writable database */
        FavoriteDbHelper helper = new FavoriteDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* Create a new row of task data */
        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,351286 );
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_BG_URL, "/gBmrsugfWpiXRh13Vo3j0WW55qD.jpg");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL, "/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_RATING, 4.5);
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, "2018-1-1");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS, "SUPER GOOD");
        testTaskValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, "SUPER MOVIE");

        /* Insert ContentValues into database and get a row ID back */
        long taskRowId = database.insert(
                /* Table to insert values into */
                FavoriteContract.FavoriteEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testTaskValues);

        /* Always close the database when you're through with it */
        database.close();

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, taskRowId != -1);


        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);



        /* The delete method deletes the previously inserted row with id = 1 */
        Uri uriToDelete = FavoriteContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath("1").build();
        int tasksDeleted = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, tasksDeleted != 0);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }



}
