import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './room-equipment.reducer';

export const RoomEquipmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const roomEquipmentEntity = useAppSelector(state => state.roomEquipment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="roomEquipmentDetailsHeading">
          <Translate contentKey="meetingsApp.roomEquipment.detail.title">RoomEquipment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{roomEquipmentEntity.id}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="meetingsApp.roomEquipment.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{roomEquipmentEntity.quantity}</dd>
          <dt>
            <Translate contentKey="meetingsApp.roomEquipment.room">Room</Translate>
          </dt>
          <dd>{roomEquipmentEntity.room ? roomEquipmentEntity.room.code : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.roomEquipment.equipment">Equipment</Translate>
          </dt>
          <dd>{roomEquipmentEntity.equipment ? roomEquipmentEntity.equipment.code : ''}</dd>
        </dl>
        <Button tag={Link} to="/room-equipment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/room-equipment/${roomEquipmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RoomEquipmentDetail;
