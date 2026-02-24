import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './meeting.reducer';

export const MeetingDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const meetingEntity = useAppSelector(state => state.meeting.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="meetingDetailsHeading">
          <Translate contentKey="meetingsApp.meeting.detail.title">Meeting</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="meetingsApp.meeting.title">Title</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.title}</dd>
          <dt>
            <span id="startTime">
              <Translate contentKey="meetingsApp.meeting.startTime">Start Time</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.startTime ? <TextFormat value={meetingEntity.startTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="endTime">
              <Translate contentKey="meetingsApp.meeting.endTime">End Time</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.endTime ? <TextFormat value={meetingEntity.endTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="mode">
              <Translate contentKey="meetingsApp.meeting.mode">Mode</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.mode}</dd>
          <dt>
            <span id="onlineLink">
              <Translate contentKey="meetingsApp.meeting.onlineLink">Online Link</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.onlineLink}</dd>
          <dt>
            <span id="objectives">
              <Translate contentKey="meetingsApp.meeting.objectives">Objectives</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.objectives}</dd>
          <dt>
            <span id="note">
              <Translate contentKey="meetingsApp.meeting.note">Note</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.note}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="meetingsApp.meeting.status">Status</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.status}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="meetingsApp.meeting.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.createdAt ? <TextFormat value={meetingEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="meetingsApp.meeting.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {meetingEntity.submittedAt ? <TextFormat value={meetingEntity.submittedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="approvedAt">
              <Translate contentKey="meetingsApp.meeting.approvedAt">Approved At</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.approvedAt ? <TextFormat value={meetingEntity.approvedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="canceledAt">
              <Translate contentKey="meetingsApp.meeting.canceledAt">Canceled At</Translate>
            </span>
          </dt>
          <dd>{meetingEntity.canceledAt ? <TextFormat value={meetingEntity.canceledAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meeting.type">Type</Translate>
          </dt>
          <dd>{meetingEntity.type ? meetingEntity.type.name : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meeting.level">Level</Translate>
          </dt>
          <dd>{meetingEntity.level ? meetingEntity.level.name : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meeting.organizerDepartment">Organizer Department</Translate>
          </dt>
          <dd>{meetingEntity.organizerDepartment ? meetingEntity.organizerDepartment.name : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meeting.room">Room</Translate>
          </dt>
          <dd>{meetingEntity.room ? meetingEntity.room.name : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meeting.requester">Requester</Translate>
          </dt>
          <dd>{meetingEntity.requester ? meetingEntity.requester.login : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meeting.host">Host</Translate>
          </dt>
          <dd>{meetingEntity.host ? meetingEntity.host.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/meeting" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/meeting/${meetingEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MeetingDetail;
