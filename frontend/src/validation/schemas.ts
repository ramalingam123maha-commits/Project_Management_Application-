import { z } from 'zod';

const optionalDate = z.string().optional().or(z.literal(''));

export const loginSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Email must be valid'),
  password: z.string().min(1, 'Password is required'),
});

export const registerSchema = z.object({
  name: z.string().trim().min(1, 'Name is required').max(100, 'Name must be at most 100 characters'),
  email: z.string().min(1, 'Email is required').email('Email must be valid').max(255),
  password: z
    .string()
    .min(8, 'Password must be at least 8 characters')
    .max(72, 'Password must be at most 72 characters')
    .regex(/^(?=.*[A-Za-z])(?=.*\d).+$/, 'Password must contain at least one letter and one digit'),
});

export const projectSchema = z
  .object({
    name: z.string().trim().min(1, 'Project name is required').max(150, 'Name must be at most 150 characters'),
    description: z.string().max(5000, 'Description must be at most 5000 characters').optional(),
    status: z.enum(['ACTIVE', 'ON_HOLD', 'COMPLETED']),
    startDate: optionalDate,
    dueDate: optionalDate,
  })
  .refine((v) => !v.startDate || !v.dueDate || v.dueDate >= v.startDate, {
    message: 'Due date must not be before start date',
    path: ['dueDate'],
  });

export const taskSchema = z.object({
  title: z.string().trim().min(1, 'Title is required').max(200, 'Title must be at most 200 characters'),
  description: z.string().max(5000, 'Description must be at most 5000 characters').optional(),
  status: z.enum(['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE']),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']),
  assigneeId: z.string().optional(),
  dueDate: optionalDate,
});

export const addMemberSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Email must be valid'),
});

export const commentSchema = z.object({
  content: z.string().trim().min(1, 'Comment is required').max(2000, 'Comment must be at most 2000 characters'),
});

export type LoginValues = z.infer<typeof loginSchema>;
export type RegisterValues = z.infer<typeof registerSchema>;
export type ProjectValues = z.infer<typeof projectSchema>;
export type TaskValues = z.infer<typeof taskSchema>;
