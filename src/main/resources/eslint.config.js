const importPlugin = require('eslint-plugin-import');
const depthOfInheritanceTreeRule = require('./depth-of-inheritance-tree');
const sonarjsPlugin = require('eslint-plugin-sonarjs');

module.exports = [
  {
    files: ['**/*.js'],
    plugins: {
      import: importPlugin,
      sonarjs: sonarjsPlugin,
      'custom-rules': { rules: { 'depth-of-inheritance-tree': depthOfInheritanceTreeRule } },
    },
    rules: {
      'no-unused-vars': 'warn',
      'no-console': 'off',
      'complexity': ['warn', 2],
      'import/no-cycle': 'warn',
      'import/max-dependencies': ['warn', { max: 2 }],
      'max-lines': ['warn', { 'max': 10, 'skipComments': true }],
      'max-lines-per-function': ['warn', { 'max': 5, 'skipComments': true, 'skipBlankLines': true }],
      'max-params': ['warn', 4],
      'custom-rules/depth-of-inheritance-tree': ['warn', 2],    },
  },
];
