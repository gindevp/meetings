import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './room.reducer';

export const RoomDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const roomEntity = useAppSelector(state => state.room.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="roomDetailsHeading">
          <Translate contentKey="meetingsApp.room.detail.title">Room</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{roomEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="meetingsApp.room.code">Code</Translate>
            </span>
          </dt>
          <dd>{roomEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="meetingsApp.room.name">Name</Translate>
            </span>
          </dt>
          <dd>{roomEntity.name}</dd>
          <dt>
            <span id="location">
              <Translate contentKey="meetingsApp.room.location">Location</Translate>
            </span>
          </dt>
          <dd>{roomEntity.location}</dd>
          <dt>
            <span id="capacity">
              <Translate contentKey="meetingsApp.room.capacity">Capacity</Translate>
            </span>
          </dt>
          <dd>{roomEntity.capacity}</dd>
          <dt>
            <span id="active">
              <Translate contentKey="meetingsApp.room.active">Active</Translate>
            </span>
          </dt>
          <dd>{roomEntity.active ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/room" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/room/${roomEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RoomDetail;
