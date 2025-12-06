import { searchTest } from "./searchTest";
import { searchTestResultData } from "./searchTestResultData";
import testUpdateService from "./testUpdateService";
import { viewTestSummary } from "./viewTestSummary";
import createService from "./createService";
export const tqmService = {
  searchTestResultData,
  viewTestSummary,
  searchTest,
  testUpdateService,
  createService
};
