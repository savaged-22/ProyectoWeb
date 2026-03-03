export interface Process { id: number; name: string; description?: string; category?: string; status: 'DRAFT' | 'PUBLISHED' | 'INACTIVE'; }
