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
      'complexity': ['warn', 100],
      'import/no-cycle': 'warn',
      'import/max-dependencies': ['warn', { max: 20 }],
      'max-lines': ['warn', { 'max': 1000, 'skipComments': true }],
      'max-lines-per-function': ['warn', { 'max': 50, 'skipComments': true, 'skipBlankLines': true }],
      'max-params': ['warn', 4],
      'custom-rules/depth-of-inheritance-tree': ['warn', 2],    },
  },
];
