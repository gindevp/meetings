import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './meeting-level.reducer';

export const MeetingLevelDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const meetingLevelEntity = useAppSelector(state => state.meetingLevel.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="meetingLevelDetailsHeading">
          <Translate contentKey="meetingsApp.meetingLevel.detail.title">MeetingLevel</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{meetingLevelEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="meetingsApp.meetingLevel.name">Name</Translate>
            </span>
          </dt>
          <dd>{meetingLevelEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="meetingsApp.meetingLevel.description">Description</Translate>
            </span>
          </dt>
          <dd>{meetingLevelEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/meeting-level" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/meeting-level/${meetingLevelEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MeetingLevelDetail;
