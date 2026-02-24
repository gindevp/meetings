import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './meeting-participant.reducer';

export const MeetingParticipantDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const meetingParticipantEntity = useAppSelector(state => state.meetingParticipant.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="meetingParticipantDetailsHeading">
          <Translate contentKey="meetingsApp.meetingParticipant.detail.title">MeetingParticipant</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{meetingParticipantEntity.id}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="meetingsApp.meetingParticipant.role">Role</Translate>
            </span>
          </dt>
          <dd>{meetingParticipantEntity.role}</dd>
          <dt>
            <span id="isRequired">
              <Translate contentKey="meetingsApp.meetingParticipant.isRequired">Is Required</Translate>
            </span>
          </dt>
          <dd>{meetingParticipantEntity.isRequired ? 'true' : 'false'}</dd>
          <dt>
            <span id="attendance">
              <Translate contentKey="meetingsApp.meetingParticipant.attendance">Attendance</Translate>
            </span>
          </dt>
          <dd>{meetingParticipantEntity.attendance}</dd>
          <dt>
            <span id="absentReason">
              <Translate contentKey="meetingsApp.meetingParticipant.absentReason">Absent Reason</Translate>
            </span>
          </dt>
          <dd>{meetingParticipantEntity.absentReason}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingParticipant.user">User</Translate>
          </dt>
          <dd>{meetingParticipantEntity.user ? meetingParticipantEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingParticipant.meeting">Meeting</Translate>
          </dt>
          <dd>{meetingParticipantEntity.meeting ? meetingParticipantEntity.meeting.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/meeting-participant" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/meeting-participant/${meetingParticipantEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MeetingParticipantDetail;
