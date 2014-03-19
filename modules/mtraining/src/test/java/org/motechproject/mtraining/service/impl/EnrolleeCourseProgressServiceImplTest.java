package org.motechproject.mtraining.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.builder.CourseProgressUpdater;
import org.motechproject.mtraining.constants.CourseStatus;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.domain.CourseConfiguration;
import org.motechproject.mtraining.domain.EnrolleeCourseProgress;
import org.motechproject.mtraining.domain.Location;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.EnrolleeCourseProgressDto;
import org.motechproject.mtraining.repository.AllCourseConfigurations;
import org.motechproject.mtraining.repository.AllEnrolleeCourseProgress;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class EnrolleeCourseProgressServiceImplTest {

    @Mock
    private CourseProgressUpdater courseProgressUpdater;
    @Mock
    private AllEnrolleeCourseProgress allCourseProgress;
    @Mock
    private AllCourseConfigurations allCourseConfigs;
    @Mock
    private BookmarkServiceImpl bookmarkService;
    private CourseProgressServiceImpl courseProgressService;
    private Location location;

    @Before
    public void setUp() throws Exception {
        courseProgressService = new CourseProgressServiceImpl(bookmarkService, allCourseProgress, courseProgressUpdater, allCourseConfigs);
        location = new Location("block", "district", "state");
    }

    @Test
    public void shouldReturnNullIfNoCourseProgressExistsForEnrollee() throws Exception {
        String externalId = "user1";

        when(allCourseProgress.findCourseProgressForOngoingCourse(externalId)).thenReturn(null);

        EnrolleeCourseProgressDto courseProgressForEnrollee = courseProgressService.getCourseProgressForEnrollee(externalId);

        assertNull(courseProgressForEnrollee);
        verifyZeroInteractions(bookmarkService);
        verifyZeroInteractions(courseProgressUpdater);
    }

    @Test
    public void shouldReturnNullIfBookmarkForAnEnrolleeIsNull() throws Exception {
        String externalId = "user1";

        EnrolleeCourseProgress enrolleeCourseProgress = new EnrolleeCourseProgress(externalId, DateTime.now(), CourseStatus.STARTED, UUID.randomUUID());

        when(allCourseProgress.findCourseProgressForOngoingCourse(externalId)).thenReturn(enrolleeCourseProgress);
        when(bookmarkService.getBookmark(externalId)).thenReturn(null);

        EnrolleeCourseProgressDto courseProgressForEnrollee = courseProgressService.getCourseProgressForEnrollee(externalId);

        assertNull(courseProgressForEnrollee);
        verifyZeroInteractions(courseProgressUpdater);
        Mockito.verify(bookmarkService).getBookmark(externalId);
    }

    @Test
    public void shouldReturnAnUpdatedCourseProgressWhenAskedForCourseProgress() throws Exception {
        String externalId = "user1";
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        BookmarkDto oldBookmark = new BookmarkDto(externalId, course);
        ContentIdentifierDto newCourse = new ContentIdentifierDto(UUID.randomUUID(), 1);
        BookmarkDto newBookmark = new BookmarkDto(externalId, newCourse);
        EnrolleeCourseProgress enrolleeCourseProgress = new EnrolleeCourseProgress(externalId, DateTime.now(), CourseStatus.STARTED, course.getContentId());
        CourseConfiguration courseConfig = new CourseConfiguration(course.getContentId(), 60, location);

        when(allCourseProgress.findCourseProgressForOngoingCourse(externalId)).thenReturn(enrolleeCourseProgress);
        when(bookmarkService.getBookmark(externalId)).thenReturn(oldBookmark);
        when(allCourseConfigs.findCourseConfigurationFor(course.getContentId())).thenReturn(courseConfig);

        EnrolleeCourseProgressDto oldEnrolleeCourseProgressDto = new EnrolleeCourseProgressDto(externalId, enrolleeCourseProgress.getCourseStartTime(), oldBookmark, enrolleeCourseProgress.getCourseStatus());
        EnrolleeCourseProgressDto newEnrolleeCourseProgressDto = new EnrolleeCourseProgressDto(externalId, enrolleeCourseProgress.getCourseStartTime(), newBookmark, enrolleeCourseProgress.getCourseStatus());

        when(courseProgressUpdater.update(any(EnrolleeCourseProgressDto.class))).thenReturn(newEnrolleeCourseProgressDto);

        EnrolleeCourseProgressDto courseProgressForEnrollee = courseProgressService.getCourseProgressForEnrollee(externalId);

        ArgumentCaptor<EnrolleeCourseProgressDto> argumentCaptor = ArgumentCaptor.forClass(EnrolleeCourseProgressDto.class);
        verify(courseProgressUpdater).update(argumentCaptor.capture());
        EnrolleeCourseProgressDto expectedEnrolleeCourseProgressDto = argumentCaptor.getValue();

        assertEquals(expectedEnrolleeCourseProgressDto.getExternalId(), externalId);
        assertEquals(expectedEnrolleeCourseProgressDto.getCourseStartTime(), oldEnrolleeCourseProgressDto.getCourseStartTime());
        assertEquals(expectedEnrolleeCourseProgressDto.getBookmarkDto(), oldEnrolleeCourseProgressDto.getBookmarkDto());
        assertEquals(expectedEnrolleeCourseProgressDto.getCourseStatus(), oldEnrolleeCourseProgressDto.getCourseStatus());
        assertEquals(newBookmark, courseProgressForEnrollee.getBookmarkDto());
        assertEquals(newEnrolleeCourseProgressDto.getCourseStartTime(), courseProgressForEnrollee.getCourseStartTime());
    }

    @Test
    public void shouldCallBookmarkServiceToGetInitialBookmark() throws Exception {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        String externalId = "user1";
        BookmarkDto bookmarkDto = new BookmarkDto(externalId, course);
        when(bookmarkService.getInitialBookmark(externalId, course)).thenReturn(bookmarkDto);
        CourseConfiguration courseConfig = new CourseConfiguration(course.getContentId(), 60, location);
        when(allCourseConfigs.findCourseConfigurationFor(course.getContentId())).thenReturn(courseConfig);

        EnrolleeCourseProgressDto enrolleeCourseProgressDto = courseProgressService.getInitialCourseProgressForEnrollee(externalId, course);

        verify(bookmarkService).getInitialBookmark(externalId, course);
        assertEquals(CourseStatus.STARTED, enrolleeCourseProgressDto.getCourseStatus());
        assertEquals(externalId, enrolleeCourseProgressDto.getBookmarkDto().getExternalId());
        assertEquals(course.getContentId(), enrolleeCourseProgressDto.getBookmarkDto().getCourse().getContentId());
        assertEquals(course.getVersion(), enrolleeCourseProgressDto.getBookmarkDto().getCourse().getVersion());
    }

    @Test
    public void shouldCallBookmarkServiceToGetInitialBookmarkAndSetCourseCompletionAsDefaultTImeLeft() throws Exception {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        String externalId = "user1";
        BookmarkDto bookmarkDto = new BookmarkDto(externalId, course);
        when(bookmarkService.getInitialBookmark(externalId, course)).thenReturn(bookmarkDto);

        EnrolleeCourseProgressDto enrolleeCourseProgressDto = courseProgressService.getInitialCourseProgressForEnrollee(externalId, course);

        verify(bookmarkService).getInitialBookmark(externalId, course);
        assertEquals(CourseStatus.STARTED, enrolleeCourseProgressDto.getCourseStatus());
        assertEquals(externalId, enrolleeCourseProgressDto.getBookmarkDto().getExternalId());
        assertEquals(course.getContentId(), enrolleeCourseProgressDto.getBookmarkDto().getCourse().getContentId());
        assertEquals(course.getVersion(), enrolleeCourseProgressDto.getBookmarkDto().getCourse().getVersion());
        assertEquals(enrolleeCourseProgressDto.getTimeLeftToCompleteCourseInHrs(), 365 * 24);
    }

    @Test
    public void shouldAddCourseProgressIfNoCourseProgressFoundAndBookmarkIsAddedOrUpdatedSuccessfully() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 2);
        String externalId = "user1";
        BookmarkDto bookmarkDto = new BookmarkDto(externalId, course);

        when(bookmarkService.addOrUpdate(any(BookmarkDto.class))).thenReturn(true);

        when(allCourseProgress.findBy(externalId, course.getContentId())).thenReturn(null);

        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto(externalId, DateTime.now(), bookmarkDto, CourseStatus.STARTED);

        courseProgressService.addOrUpdateCourseProgress(enrolleeCourseProgressDto);

        EnrolleeCourseProgress expectedEnrolleeCourseProgress
                = new EnrolleeCourseProgress(enrolleeCourseProgressDto.getExternalId(),
                ISODateTimeUtil.parseWithTimeZoneUTC(enrolleeCourseProgressDto.getCourseStartTime()),
                enrolleeCourseProgressDto.getCourseStatus(), course.getContentId()
        );
        ArgumentCaptor<EnrolleeCourseProgress> courseProgressArgumentCaptor = ArgumentCaptor.forClass(EnrolleeCourseProgress.class);
        verify(allCourseProgress).add(courseProgressArgumentCaptor.capture());

        EnrolleeCourseProgress enrolleeCourseProgress = courseProgressArgumentCaptor.getValue();

        assertEquals(expectedEnrolleeCourseProgress.getExternalId(), enrolleeCourseProgress.getExternalId());
        assertEquals(expectedEnrolleeCourseProgress.getCourseStatus(), enrolleeCourseProgress.getCourseStatus());
        assertEquals(course.getContentId(), enrolleeCourseProgress.getCourseContentId());
        assertEquals(expectedEnrolleeCourseProgress.getCourseStartTime(), enrolleeCourseProgress.getCourseStartTime());
    }

    @Test
    public void shouldUpdateCourseProgressIfCourseProgressExistAndBookmarkIsAddedOrUpdatedSuccessfully() {
        DateTime now = DateTime.now();
        String externalId = "user1";
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 2);
        BookmarkDto bookmarkDto = new BookmarkDto(externalId, course);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto(externalId, ISODateTimeUtil.parseWithTimeZoneUTC(now.toString()), bookmarkDto, CourseStatus.STARTED);
        ContentIdentifier courseIdentifier = toContentIdentifier(course);
        EnrolleeCourseProgress enrolleeCourseProgressFromDb = new EnrolleeCourseProgress(externalId, ISODateTimeUtil.parseWithTimeZoneUTC(now.toString()), CourseStatus.STARTED, courseIdentifier.getContentId());

        when(bookmarkService.addOrUpdate(any(BookmarkDto.class))).thenReturn(true);

        when(allCourseProgress.findBy(externalId, courseIdentifier.getContentId())).thenReturn(enrolleeCourseProgressFromDb);

        courseProgressService.addOrUpdateCourseProgress(enrolleeCourseProgressDto);

        ArgumentCaptor<EnrolleeCourseProgress> courseProgressArgumentCaptor = ArgumentCaptor.forClass(EnrolleeCourseProgress.class);
        verify(allCourseProgress).update(courseProgressArgumentCaptor.capture());

        EnrolleeCourseProgress enrolleeCourseProgress = courseProgressArgumentCaptor.getValue();

        assertEquals(enrolleeCourseProgressDto.getExternalId(), enrolleeCourseProgress.getExternalId());
        assertEquals(enrolleeCourseProgressDto.getCourseStatus(), enrolleeCourseProgress.getCourseStatus());
        assertEquals(courseIdentifier.getContentId(), enrolleeCourseProgress.getCourseContentId());
        assertEquals(ISODateTimeUtil.parseWithTimeZoneUTC(now.toString()), enrolleeCourseProgress.getCourseStartTime());
    }

    @Test
    public void shouldMarkCourseAsCompletedAndSetBookmarkToLAstActiveContentOfACourse() {
        DateTime now = DateTime.now();
        String externalId = "user1";
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 2);

        BookmarkDto bookmarkDto = new BookmarkDto(externalId, course);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto(externalId, ISODateTimeUtil.parseWithTimeZoneUTC(now.toString()), bookmarkDto, CourseStatus.STARTED);
        ContentIdentifier courseIdentifier = toContentIdentifier(bookmarkDto.getCourse());
        EnrolleeCourseProgress enrolleeCourseProgressFromDb = new EnrolleeCourseProgress(externalId, ISODateTimeUtil.parseWithTimeZoneUTC(now.toString()), CourseStatus.STARTED, courseIdentifier.getContentId());

        when(allCourseProgress.findBy(externalId, course.getContentId())).thenReturn(enrolleeCourseProgressFromDb);

        courseProgressService.markCourseAsComplete(externalId, now.toString(), course);


        verify(bookmarkService).setBookmarkToLastActiveContentOfACourse(externalId, course);
        ArgumentCaptor<EnrolleeCourseProgress> courseProgressArgumentCaptor = ArgumentCaptor.forClass(EnrolleeCourseProgress.class);
        verify(allCourseProgress).update(courseProgressArgumentCaptor.capture());

        EnrolleeCourseProgress enrolleeCourseProgress = courseProgressArgumentCaptor.getValue();

        assertEquals(enrolleeCourseProgressDto.getExternalId(), enrolleeCourseProgress.getExternalId());
        assertEquals(CourseStatus.COMPLETED, enrolleeCourseProgress.getCourseStatus());
        assertEquals(courseIdentifier.getContentId(), enrolleeCourseProgress.getCourseContentId());
        assertEquals(ISODateTimeUtil.parseWithTimeZoneUTC(now.toString()), enrolleeCourseProgress.getCourseStartTime());
    }

    private ContentIdentifier toContentIdentifier(ContentIdentifierDto contentIdentifierDto) {
        return new ContentIdentifier(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion());
    }
}
