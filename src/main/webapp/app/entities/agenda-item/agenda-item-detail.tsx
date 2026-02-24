import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './agenda-item.reducer';

export const AgendaItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const agendaItemEntity = useAppSelector(state => state.agendaItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="agendaItemDetailsHeading">
          <Translate contentKey="meetingsApp.agendaItem.detail.title">AgendaItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{agendaItemEntity.id}</dd>
          <dt>
            <span id="itemOrder">
              <Translate contentKey="meetingsApp.agendaItem.itemOrder">Item Order</Translate>
            </span>
          </dt>
          <dd>{agendaItemEntity.itemOrder}</dd>
          <dt>
            <span id="topic">
              <Translate contentKey="meetingsApp.agendaItem.topic">Topic</Translate>
            </span>
          </dt>
          <dd>{agendaItemEntity.topic}</dd>
          <dt>
            <span id="presenterName">
              <Translate contentKey="meetingsApp.agendaItem.presenterName">Presenter Name</Translate>
            </span>
          </dt>
          <dd>{agendaItemEntity.presenterName}</dd>
          <dt>
            <span id="durationMinutes">
              <Translate contentKey="meetingsApp.agendaItem.durationMinutes">Duration Minutes</Translate>
            </span>
          </dt>
          <dd>{agendaItemEntity.durationMinutes}</dd>
          <dt>
            <span id="note">
              <Translate contentKey="meetingsApp.agendaItem.note">Note</Translate>
            </span>
          </dt>
          <dd>{agendaItemEntity.note}</dd>
          <dt>
            <Translate contentKey="meetingsApp.agendaItem.meeting">Meeting</Translate>
          </dt>
          <dd>{agendaItemEntity.meeting ? agendaItemEntity.meeting.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/agenda-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/agenda-item/${agendaItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AgendaItemDetail;
