import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './meeting-approval.reducer';

export const MeetingApprovalDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const meetingApprovalEntity = useAppSelector(state => state.meetingApproval.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="meetingApprovalDetailsHeading">
          <Translate contentKey="meetingsApp.meetingApproval.detail.title">MeetingApproval</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{meetingApprovalEntity.id}</dd>
          <dt>
            <span id="step">
              <Translate contentKey="meetingsApp.meetingApproval.step">Step</Translate>
            </span>
          </dt>
          <dd>{meetingApprovalEntity.step}</dd>
          <dt>
            <span id="decision">
              <Translate contentKey="meetingsApp.meetingApproval.decision">Decision</Translate>
            </span>
          </dt>
          <dd>{meetingApprovalEntity.decision}</dd>
          <dt>
            <span id="reason">
              <Translate contentKey="meetingsApp.meetingApproval.reason">Reason</Translate>
            </span>
          </dt>
          <dd>{meetingApprovalEntity.reason}</dd>
          <dt>
            <span id="decidedAt">
              <Translate contentKey="meetingsApp.meetingApproval.decidedAt">Decided At</Translate>
            </span>
          </dt>
          <dd>
            {meetingApprovalEntity.decidedAt ? (
              <TextFormat value={meetingApprovalEntity.decidedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingApproval.decidedBy">Decided By</Translate>
          </dt>
          <dd>{meetingApprovalEntity.decidedBy ? meetingApprovalEntity.decidedBy.login : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingApproval.meeting">Meeting</Translate>
          </dt>
          <dd>{meetingApprovalEntity.meeting ? meetingApprovalEntity.meeting.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/meeting-approval" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/meeting-approval/${meetingApprovalEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MeetingApprovalDetail;
