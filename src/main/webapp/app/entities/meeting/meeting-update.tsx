import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getMeetingTypes } from 'app/entities/meeting-type/meeting-type.reducer';
import { getEntities as getMeetingLevels } from 'app/entities/meeting-level/meeting-level.reducer';
import { getEntities as getDepartments } from 'app/entities/department/department.reducer';
import { getEntities as getRooms } from 'app/entities/room/room.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { MeetingMode } from 'app/shared/model/enumerations/meeting-mode.model';
import { MeetingStatus } from 'app/shared/model/enumerations/meeting-status.model';
import { createEntity, getEntity, reset, updateEntity } from './meeting.reducer';

export const MeetingUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const meetingTypes = useAppSelector(state => state.meetingType.entities);
  const meetingLevels = useAppSelector(state => state.meetingLevel.entities);
  const departments = useAppSelector(state => state.department.entities);
  const rooms = useAppSelector(state => state.room.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const meetingEntity = useAppSelector(state => state.meeting.entity);
  const loading = useAppSelector(state => state.meeting.loading);
  const updating = useAppSelector(state => state.meeting.updating);
  const updateSuccess = useAppSelector(state => state.meeting.updateSuccess);
  const meetingModeValues = Object.keys(MeetingMode);
  const meetingStatusValues = Object.keys(MeetingStatus);

  const handleClose = () => {
    navigate(`/meeting${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMeetingTypes({}));
    dispatch(getMeetingLevels({}));
    dispatch(getDepartments({}));
    dispatch(getRooms({}));
    dispatch(getUsers({}));
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
    values.startTime = convertDateTimeToServer(values.startTime);
    values.endTime = convertDateTimeToServer(values.endTime);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.submittedAt = convertDateTimeToServer(values.submittedAt);
    values.approvedAt = convertDateTimeToServer(values.approvedAt);
    values.canceledAt = convertDateTimeToServer(values.canceledAt);

    const entity = {
      ...meetingEntity,
      ...values,
      type: meetingTypes.find(it => it.id.toString() === values.type?.toString()),
      level: meetingLevels.find(it => it.id.toString() === values.level?.toString()),
      organizerDepartment: departments.find(it => it.id.toString() === values.organizerDepartment?.toString()),
      room: rooms.find(it => it.id.toString() === values.room?.toString()),
      requester: users.find(it => it.id.toString() === values.requester?.toString()),
      host: users.find(it => it.id.toString() === values.host?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          startTime: displayDefaultDateTime(),
          endTime: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
          submittedAt: displayDefaultDateTime(),
          approvedAt: displayDefaultDateTime(),
          canceledAt: displayDefaultDateTime(),
        }
      : {
          mode: 'IN_PERSON',
          status: 'DRAFT',
          ...meetingEntity,
          startTime: convertDateTimeFromServer(meetingEntity.startTime),
          endTime: convertDateTimeFromServer(meetingEntity.endTime),
          createdAt: convertDateTimeFromServer(meetingEntity.createdAt),
          submittedAt: convertDateTimeFromServer(meetingEntity.submittedAt),
          approvedAt: convertDateTimeFromServer(meetingEntity.approvedAt),
          canceledAt: convertDateTimeFromServer(meetingEntity.canceledAt),
          type: meetingEntity?.type?.id,
          level: meetingEntity?.level?.id,
          organizerDepartment: meetingEntity?.organizerDepartment?.id,
          room: meetingEntity?.room?.id,
          requester: meetingEntity?.requester?.id,
          host: meetingEntity?.host?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="meetingsApp.meeting.home.createOrEditLabel" data-cy="MeetingCreateUpdateHeading">
            <Translate contentKey="meetingsApp.meeting.home.createOrEditLabel">Create or edit a Meeting</Translate>
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
                  id="meeting-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('meetingsApp.meeting.title')}
                id="meeting-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.meeting.startTime')}
                id="meeting-startTime"
                name="startTime"
                data-cy="startTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.meeting.endTime')}
                id="meeting-endTime"
                name="endTime"
                data-cy="endTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('meetingsApp.meeting.mode')} id="meeting-mode" name="mode" data-cy="mode" type="select">
                {meetingModeValues.map(meetingMode => (
                  <option value={meetingMode} key={meetingMode}>
                    {translate(`meetingsApp.MeetingMode.${meetingMode}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('meetingsApp.meeting.onlineLink')}
                id="meeting-onlineLink"
                name="onlineLink"
                data-cy="onlineLink"
                type="text"
              />
              <ValidatedField
                label={translate('meetingsApp.meeting.objectives')}
                id="meeting-objectives"
                name="objectives"
                data-cy="objectives"
                type="textarea"
              />
              <ValidatedField label={translate('meetingsApp.meeting.note')} id="meeting-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField
                label={translate('meetingsApp.meeting.status')}
                id="meeting-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {meetingStatusValues.map(meetingStatus => (
                  <option value={meetingStatus} key={meetingStatus}>
                    {translate(`meetingsApp.MeetingStatus.${meetingStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('meetingsApp.meeting.createdAt')}
                id="meeting-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.meeting.submittedAt')}
                id="meeting-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('meetingsApp.meeting.approvedAt')}
                id="meeting-approvedAt"
                name="approvedAt"
                data-cy="approvedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('meetingsApp.meeting.canceledAt')}
                id="meeting-canceledAt"
                name="canceledAt"
                data-cy="canceledAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="meeting-type"
                name="type"
                data-cy="type"
                label={translate('meetingsApp.meeting.type')}
                type="select"
                required
              >
                <option value="" key="0" />
                {meetingTypes
                  ? meetingTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="meeting-level"
                name="level"
                data-cy="level"
                label={translate('meetingsApp.meeting.level')}
                type="select"
                required
              >
                <option value="" key="0" />
                {meetingLevels
                  ? meetingLevels.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="meeting-organizerDepartment"
                name="organizerDepartment"
                data-cy="organizerDepartment"
                label={translate('meetingsApp.meeting.organizerDepartment')}
                type="select"
                required
              >
                <option value="" key="0" />
                {departments
                  ? departments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField id="meeting-room" name="room" data-cy="room" label={translate('meetingsApp.meeting.room')} type="select">
                <option value="" key="0" />
                {rooms
                  ? rooms.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="meeting-requester"
                name="requester"
                data-cy="requester"
                label={translate('meetingsApp.meeting.requester')}
                type="select"
                required
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="meeting-host"
                name="host"
                data-cy="host"
                label={translate('meetingsApp.meeting.host')}
                type="select"
                required
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/meeting" replace color="info">
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

export default MeetingUpdate;
