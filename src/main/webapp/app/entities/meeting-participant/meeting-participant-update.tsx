import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getMeetings } from 'app/entities/meeting/meeting.reducer';
import { ParticipantRole } from 'app/shared/model/enumerations/participant-role.model';
import { AttendanceStatus } from 'app/shared/model/enumerations/attendance-status.model';
import { createEntity, getEntity, reset, updateEntity } from './meeting-participant.reducer';

export const MeetingParticipantUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const meetings = useAppSelector(state => state.meeting.entities);
  const meetingParticipantEntity = useAppSelector(state => state.meetingParticipant.entity);
  const loading = useAppSelector(state => state.meetingParticipant.loading);
  const updating = useAppSelector(state => state.meetingParticipant.updating);
  const updateSuccess = useAppSelector(state => state.meetingParticipant.updateSuccess);
  const participantRoleValues = Object.keys(ParticipantRole);
  const attendanceStatusValues = Object.keys(AttendanceStatus);

  const handleClose = () => {
    navigate('/meeting-participant');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
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

    const entity = {
      ...meetingParticipantEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
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
          role: 'HOST',
          attendance: 'NOT_MARKED',
          ...meetingParticipantEntity,
          user: meetingParticipantEntity?.user?.id,
          meeting: meetingParticipantEntity?.meeting?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="meetingsApp.meetingParticipant.home.createOrEditLabel" data-cy="MeetingParticipantCreateUpdateHeading">
            <Translate contentKey="meetingsApp.meetingParticipant.home.createOrEditLabel">Create or edit a MeetingParticipant</Translate>
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
                  id="meeting-participant-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('meetingsApp.meetingParticipant.role')}
                id="meeting-participant-role"
                name="role"
                data-cy="role"
                type="select"
              >
                {participantRoleValues.map(participantRole => (
                  <option value={participantRole} key={participantRole}>
                    {translate(`meetingsApp.ParticipantRole.${participantRole}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('meetingsApp.meetingParticipant.isRequired')}
                id="meeting-participant-isRequired"
                name="isRequired"
                data-cy="isRequired"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('meetingsApp.meetingParticipant.attendance')}
                id="meeting-participant-attendance"
                name="attendance"
                data-cy="attendance"
                type="select"
              >
                {attendanceStatusValues.map(attendanceStatus => (
                  <option value={attendanceStatus} key={attendanceStatus}>
                    {translate(`meetingsApp.AttendanceStatus.${attendanceStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('meetingsApp.meetingParticipant.absentReason')}
                id="meeting-participant-absentReason"
                name="absentReason"
                data-cy="absentReason"
                type="text"
              />
              <ValidatedField
                id="meeting-participant-user"
                name="user"
                data-cy="user"
                label={translate('meetingsApp.meetingParticipant.user')}
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
                id="meeting-participant-meeting"
                name="meeting"
                data-cy="meeting"
                label={translate('meetingsApp.meetingParticipant.meeting')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/meeting-participant" replace color="info">
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

export default MeetingParticipantUpdate;
