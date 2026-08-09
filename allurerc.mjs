const reportName = process.env.REPORT_NAME || 'OrangeHRM Report';

export default {
  name: reportName,
  output: './target/allure-report',
  plugins: {
    awesome: {
      options: {
        reportName,
      },
    },
  },
};
