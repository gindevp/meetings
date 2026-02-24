import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './meeting-task.reducer';

export const MeetingTaskDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const meetingTaskEntity = useAppSelector(state => state.meetingTask.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="meetingTaskDetailsHeading">
          <Translate contentKey="meetingsApp.meetingTask.detail.title">MeetingTask</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{meetingTaskEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="meetingsApp.meetingTask.type">Type</Translate>
            </span>
          </dt>
          <dd>{meetingTaskEntity.type}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="meetingsApp.meetingTask.title">Title</Translate>
            </span>
          </dt>
          <dd>{meetingTaskEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="meetingsApp.meetingTask.description">Description</Translate>
            </span>
          </dt>
          <dd>{meetingTaskEntity.description}</dd>
          <dt>
            <span id="dueAt">
              <Translate contentKey="meetingsApp.meetingTask.dueAt">Due At</Translate>
            </span>
          </dt>
          <dd>{meetingTaskEntity.dueAt ? <TextFormat value={meetingTaskEntity.dueAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="meetingsApp.meetingTask.status">Status</Translate>
            </span>
          </dt>
          <dd>{meetingTaskEntity.status}</dd>
          <dt>
            <span id="remindBeforeMinutes">
              <Translate contentKey="meetingsApp.meetingTask.remindBeforeMinutes">Remind Before Minutes</Translate>
            </span>
          </dt>
          <dd>{meetingTaskEntity.remindBeforeMinutes}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingTask.assignee">Assignee</Translate>
          </dt>
          <dd>{meetingTaskEntity.assignee ? meetingTaskEntity.assignee.login : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingTask.assignedBy">Assigned By</Translate>
          </dt>
          <dd>{meetingTaskEntity.assignedBy ? meetingTaskEntity.assignedBy.login : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingTask.meeting">Meeting</Translate>
          </dt>
          <dd>{meetingTaskEntity.meeting ? meetingTaskEntity.meeting.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/meeting-task" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/meeting-task/${meetingTaskEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MeetingTaskDetail;
