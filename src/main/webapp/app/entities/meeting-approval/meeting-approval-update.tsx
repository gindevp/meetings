import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getMeetings } from 'app/entities/meeting/meeting.reducer';
import { ApprovalDecision } from 'app/shared/model/enumerations/approval-decision.model';
import { createEntity, getEntity, reset, updateEntity } from './meeting-approval.reducer';

export const MeetingApprovalUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const meetings = useAppSelector(state => state.meeting.entities);
  const meetingApprovalEntity = useAppSelector(state => state.meetingApproval.entity);
  const loading = useAppSelector(state => state.meetingApproval.loading);
  const updating = useAppSelector(state => state.meetingApproval.updating);
  const updateSuccess = useAppSelector(state => state.meetingApproval.updateSuccess);
  const approvalDecisionValues = Object.keys(ApprovalDecision);

  const handleClose = () => {
    navigate('/meeting-approval');
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
    if (values.step !== undefined && typeof values.step !== 'number') {
      values.step = Number(values.step);
    }
    values.decidedAt = convertDateTimeToServer(values.decidedAt);

    const entity = {
      ...meetingApprovalEntity,
      ...values,
      decidedBy: users.find(it => it.id.toString() === values.decidedBy?.toString()),
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
      ? {
          decidedAt: displayDefaultDateTime(),
        }
      : {
          decision: 'APPROVED',
          ...meetingApprovalEntity,
          decidedAt: convertDateTimeFromServer(meetingApprovalEntity.decidedAt),
          decidedBy: meetingApprovalEntity?.decidedBy?.id,
          meeting: meetingApprovalEntity?.meeting?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="meetingsApp.meetingApproval.home.createOrEditLabel" data-cy="MeetingApprovalCreateUpdateHeading">
            <Translate contentKey="meetingsApp.meetingApproval.home.createOrEditLabel">Create or edit a MeetingApproval</Translate>
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
                  id="meeting-approval-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('meetingsApp.meetingApproval.step')}
                id="meeting-approval-step"
                name="step"
                data-cy="step"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.meetingApproval.decision')}
                id="meeting-approval-decision"
                name="decision"
                data-cy="decision"
                type="select"
              >
                {approvalDecisionValues.map(approvalDecision => (
                  <option value={approvalDecision} key={approvalDecision}>
                    {translate(`meetingsApp.ApprovalDecision.${approvalDecision}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('meetingsApp.meetingApproval.reason')}
                id="meeting-approval-reason"
                name="reason"
                data-cy="reason"
                type="text"
              />
              <ValidatedField
                label={translate('meetingsApp.meetingApproval.decidedAt')}
                id="meeting-approval-decidedAt"
                name="decidedAt"
                data-cy="decidedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="meeting-approval-decidedBy"
                name="decidedBy"
                data-cy="decidedBy"
                label={translate('meetingsApp.meetingApproval.decidedBy')}
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
                id="meeting-approval-meeting"
                name="meeting"
                data-cy="meeting"
                label={translate('meetingsApp.meetingApproval.meeting')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/meeting-approval" replace color="info">
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

export default MeetingApprovalUpdate;
