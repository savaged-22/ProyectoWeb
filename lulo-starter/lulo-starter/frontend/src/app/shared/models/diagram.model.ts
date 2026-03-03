export interface Activity { id: number; processId: number; name: string; type: string; laneId?: number; }
export interface Arc { id: number; processId: number; fromId: number; toId: number; }
export interface Gateway { id: number; processId: number; type: 'XOR' | 'AND' | 'OR'; }
export interface Lane { id: number; processId: number; name: string; }
