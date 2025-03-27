import { getLocalization } from '@toolpad/core/locales/getLocalization';

const zh = {
  // Account
  accountSignInLabel: '登录',
  accountSignOutLabel: '登出',
  // AccountPreview
  accountPreviewTitle: '账户',
  accountPreviewIconButtonLabel: '当前用户',
  // SignInPage
  signInTitle: '登录',
  signInSubtitle: '欢迎用户，请登录以继续',
  signInRememberMe: '记住我',
  oauthSignInTitle: '使用 OAuth 登录',
  passkeySignInTitle: '使用密码登录',
  magicLinkSignInTitle: '使用魔法链接登录',
  // Common authentication labels
  email: '电子邮件',
  password: '密码',
  username: '用户名',
  passkey: '密码',
  // Common action labels
  save: '保存',
  cancel: '取消',
  ok: '确定',
  or: '或',
  to: '到',
  with: '使用',
  close: '关闭',
  delete: '删除',
  alert: '警告',
  confirm: '确认',
  loading: '加载中...',
  // CRUD
  createNewButtonLabel: '新建',
  reloadButtonLabel: '重新加载数据',
  createLabel: '创建',
  createSuccessMessage: '项目创建成功。',
  createErrorMessage: '创建项目失败。原因：',
  editLabel: '编辑',
  editSuccessMessage: '项目编辑成功。',
  editErrorMessage: '编辑项目失败。原因：',
  deleteLabel: '删除',
  deleteConfirmTitle: '删除项目？',
  deleteConfirmMessage: '您确定要删除此项目吗？',
  deleteConfirmLabel: '删除',
  deleteCancelLabel: '取消',
  deleteSuccessMessage: '项目删除成功。',
  deleteErrorMessage: '删除项目失败。原因：',
  deletedItemMessage: '该项目已被删除。',
};

export default getLocalization(zh);
