package com.anupcowkur.mvpsample.ui.presenters;

import android.test.suitebuilder.annotation.SmallTest;

import com.anupcowkur.mvpsample.model.PostsAPI;
import com.anupcowkur.mvpsample.model.data.Post;
import com.anupcowkur.mvpsample.ui.activities.PostsActivity;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Observable.class, AndroidSchedulers.class})
public class PostsPresenterTest extends TestCase {

    private PostsPresenter postsPresenter;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        postsPresenter = spy(new PostsPresenter());

    }

    @Test
    @SmallTest
    public void testShouldInitRecyclerView() {
        PostsActivity postsActivity = Mockito.mock(PostsActivity.class);

        postsPresenter.initRecyclerView(postsActivity);

        verify(postsActivity, times(1)).initRecyclerViewUI();
        verify(postsActivity, times(1)).initRecyclerViewAdapter();

    }

    @Test
    @SmallTest
    public void testShouldSchedulePostsLoadFromAPIOnBackgroundThread() {

        //create mocks
        Observable<List<Post>> postsObservable = (Observable<List<Post>>) mock(Observable.class);
        postsPresenter.postsAPI = mock(PostsAPI.class);
        postsPresenter.postsSubscriber = (Subscriber<List<Post>>) mock(Subscriber.class);

        //define return values
        when(postsPresenter.postsAPI.getPostsObservable()).thenReturn(postsObservable);
        when(postsObservable.subscribeOn(Schedulers.io())).thenReturn(postsObservable);
        when(postsObservable.observeOn(AndroidSchedulers.mainThread())).thenReturn(postsObservable);

        //call test method
        postsPresenter.loadPostsFromAPI();

        //verify if all methods in the chain are called with correct arguments

        verify(postsPresenter.postsAPI, times(1)).getPostsObservable();
        verify(postsObservable, times(1)).subscribeOn(Schedulers.io());
        verify(postsObservable, times(1)).observeOn(AndroidSchedulers.mainThread());
        verify(postsObservable, times(1)).subscribe(postsPresenter.postsSubscriber);
    }

}
