// utils/asyncHandler.ts
import { Request, Response, NextFunction } from "express";
import { ParamsDictionary } from 'express-serve-static-core';
import * as QueryString from 'qs';

const asyncHandler = (
  fn: (
    req: Request<ParamsDictionary, any, any, QueryString.ParsedQs>,
    res: Response,
    next: NextFunction
  ) => Promise<void>
) => {
  return (req: Request, res: Response, next: NextFunction) => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
};

export default asyncHandler;
