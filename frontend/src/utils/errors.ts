import axios from 'axios';
import type { ApiError } from '../types';

export interface ParsedError {
  message: string;
  fieldErrors: Record<string, string>;
}

/** Turns any thrown value into a message that is safe and useful to show the user. */
export function parseError(err: unknown): ParsedError {
  if (axios.isAxiosError(err)) {
    if (!err.response) {
      return { message: 'Cannot reach the server. Please try again.', fieldErrors: {} };
    }
    const data = err.response.data as Partial<ApiError> | undefined;
    return {
      message: data?.message || `Request failed (${err.response.status})`,
      fieldErrors: data?.fieldErrors ?? {},
    };
  }
  return { message: 'Something went wrong. Please try again.', fieldErrors: {} };
}
