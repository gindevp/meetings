import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './meeting-type.reducer';

export const MeetingTypeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const meetingTypeEntity = useAppSelector(state => state.meetingType.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="meetingTypeDetailsHeading">
          <Translate contentKey="meetingsApp.meetingType.detail.title">MeetingType</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{meetingTypeEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="meetingsApp.meetingType.name">Name</Translate>
            </span>
          </dt>
          <dd>{meetingTypeEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="meetingsApp.meetingType.description">Description</Translate>
            </span>
          </dt>
          <dd>{meetingTypeEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/meeting-type" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/meeting-type/${meetingTypeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MeetingTypeDetail;
