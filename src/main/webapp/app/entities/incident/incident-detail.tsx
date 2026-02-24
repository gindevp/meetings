import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './incident.reducer';

export const IncidentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const incidentEntity = useAppSelector(state => state.incident.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="incidentDetailsHeading">
          <Translate contentKey="meetingsApp.incident.detail.title">Incident</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{incidentEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="meetingsApp.incident.title">Title</Translate>
            </span>
          </dt>
          <dd>{incidentEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="meetingsApp.incident.description">Description</Translate>
            </span>
          </dt>
          <dd>{incidentEntity.description}</dd>
          <dt>
            <span id="reportedAt">
              <Translate contentKey="meetingsApp.incident.reportedAt">Reported At</Translate>
            </span>
          </dt>
          <dd>
            {incidentEntity.reportedAt ? <TextFormat value={incidentEntity.reportedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="severity">
              <Translate contentKey="meetingsApp.incident.severity">Severity</Translate>
            </span>
          </dt>
          <dd>{incidentEntity.severity}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="meetingsApp.incident.status">Status</Translate>
            </span>
          </dt>
          <dd>{incidentEntity.status}</dd>
          <dt>
            <Translate contentKey="meetingsApp.incident.reportedBy">Reported By</Translate>
          </dt>
          <dd>{incidentEntity.reportedBy ? incidentEntity.reportedBy.login : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.incident.meeting">Meeting</Translate>
          </dt>
          <dd>{incidentEntity.meeting ? incidentEntity.meeting.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/incident" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/incident/${incidentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IncidentDetail;
