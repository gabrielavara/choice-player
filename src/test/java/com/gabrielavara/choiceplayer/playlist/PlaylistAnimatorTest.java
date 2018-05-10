package com.gabrielavara.choiceplayer.playlist;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;

import de.saxsys.javafx.test.JfxRunner;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

@RunWith(JfxRunner.class)
public class PlaylistAnimatorTest {
    @Mock
    private StackPane playlistStackPaneMock;
    @Mock
    private ObservableList<Node> playlistStackPaneChildrenMock;
    @Mock
    private JFXSpinner spinnerMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private JFXListView<PlaylistItemView> playlistViewMock;
    @Mock
    private ReadOnlyObjectProperty<PlaylistItemView> playlistItemViewReadOnlyObjectPropertyMock;

    private PlaylistAnimator playlistAnimator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(playlistStackPaneMock.getChildren()).thenReturn(playlistStackPaneChildrenMock);
        when(playlistViewMock.getSelectionModel().selectedItemProperty()).thenReturn(playlistItemViewReadOnlyObjectPropertyMock);
        playlistAnimator = new PlaylistAnimator(playlistViewMock, spinnerMock, playlistStackPaneMock);
    }

    @Test
    public void testShowItems() {
        //when
        playlistAnimator.showItems(Optional.empty(), false);

        // then
        Awaitility.with().pollInterval(250, MILLISECONDS).await()
                .atMost(2, SECONDS).untilAsserted(new SpinnerRemoved());
    }

    @Test
    public void testAnimateOutItems() {
        //when
        playlistAnimator.animateOutItems(null, Optional.empty());

        // then
        Awaitility.with().pollInterval(250, MILLISECONDS).await()
                .atMost(2, SECONDS).untilAsserted(new SpinnerAdded());
        verify(playlistStackPaneChildrenMock, never()).remove(spinnerMock);
    }

    class SpinnerRemoved implements ThrowingRunnable {
        @Override
        public void run() throws Throwable {
            verify(playlistStackPaneChildrenMock).remove(spinnerMock);
        }
    }

    class SpinnerAdded implements ThrowingRunnable {
        @Override
        public void run() throws Throwable {
            verify(playlistStackPaneChildrenMock).add(spinnerMock);
        }
    }
}

