'use client';
import { createTheme } from '@mui/material/styles';
import { zhCN as coreZhCN } from '@mui/material/locale';
import zhCN from './zhCN';

const theme = createTheme(
  {
    cssVariables: {
      colorSchemeSelector: 'data-toolpad-color-scheme',
    },
    colorSchemes: { light: true, dark: true },
  },
  coreZhCN,
  zhCN,
);

export default theme;
