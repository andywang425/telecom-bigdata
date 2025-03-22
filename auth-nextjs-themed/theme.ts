'use client';
import { createTheme } from '@mui/material/styles';
import getMPTheme from './theme/getMPTheme';
import { zhCN as coreZhCN } from '@mui/material/locale';
import zhCN from './zhCN';

const lightTheme = createTheme(getMPTheme('light'), coreZhCN, zhCN);
const darkTheme = createTheme(getMPTheme('dark'), coreZhCN, zhCN);

const theme = {
  light: lightTheme,
  dark: darkTheme,
};

export default theme;
