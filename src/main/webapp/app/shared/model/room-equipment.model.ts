import { IRoom } from 'app/shared/model/room.model';
import { IEquipment } from 'app/shared/model/equipment.model';

export interface IRoomEquipment {
  id?: number;
  quantity?: number;
  room?: IRoom;
  equipment?: IEquipment;
}

export const defaultValue: Readonly<IRoomEquipment> = {};
