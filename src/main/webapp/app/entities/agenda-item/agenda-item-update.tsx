import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getMeetings } from 'app/entities/meeting/meeting.reducer';
import { createEntity, getEntity, reset, updateEntity } from './agenda-item.reducer';

export const AgendaItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const meetings = useAppSelector(state => state.meeting.entities);
  const agendaItemEntity = useAppSelector(state => state.agendaItem.entity);
  const loading = useAppSelector(state => state.agendaItem.loading);
  const updating = useAppSelector(state => state.agendaItem.updating);
  const updateSuccess = useAppSelector(state => state.agendaItem.updateSuccess);

  const handleClose = () => {
    navigate('/agenda-item');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMeetings({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.itemOrder !== undefined && typeof values.itemOrder !== 'number') {
      values.itemOrder = Number(values.itemOrder);
    }
    if (values.durationMinutes !== undefined && typeof values.durationMinutes !== 'number') {
      values.durationMinutes = Number(values.durationMinutes);
    }

    const entity = {
      ...agendaItemEntity,
      ...values,
      meeting: meetings.find(it => it.id.toString() === values.meeting?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...agendaItemEntity,
          meeting: agendaItemEntity?.meeting?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="meetingsApp.agendaItem.home.createOrEditLabel" data-cy="AgendaItemCreateUpdateHeading">
            <Translate contentKey="meetingsApp.agendaItem.home.createOrEditLabel">Create or edit a AgendaItem</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="agenda-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('meetingsApp.agendaItem.itemOrder')}
                id="agenda-item-itemOrder"
                name="itemOrder"
                data-cy="itemOrder"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.agendaItem.topic')}
                id="agenda-item-topic"
                name="topic"
                data-cy="topic"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.agendaItem.presenterName')}
                id="agenda-item-presenterName"
                name="presenterName"
                data-cy="presenterName"
                type="text"
              />
              <ValidatedField
                label={translate('meetingsApp.agendaItem.durationMinutes')}
                id="agenda-item-durationMinutes"
                name="durationMinutes"
                data-cy="durationMinutes"
                type="text"
              />
              <ValidatedField
                label={translate('meetingsApp.agendaItem.note')}
                id="agenda-item-note"
                name="note"
                data-cy="note"
                type="textarea"
              />
              <ValidatedField
                id="agenda-item-meeting"
                name="meeting"
                data-cy="meeting"
                label={translate('meetingsApp.agendaItem.meeting')}
                type="select"
                required
              >
                <option value="" key="0" />
                {meetings
                  ? meetings.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/agenda-item" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default AgendaItemUpdate;
