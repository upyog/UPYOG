import React from 'react';

const DonutChart = ({ data }) => {
  if (data.length !== 2) {
    throw new Error('This donut chart requires exactly 2 values.');
  }

  const total = data.reduce((sum, item) => sum + item.value, 0);
  const colors = ['#dd2f03', '#198754']; // Define colors for the two segments

  return (
    <div className="donut-chart">
      <style>
        {`
          .donut-chart {
            position: relative;
            width: 200px;
            height: 200px;
            border-radius: 50%;
            overflow: hidden;
          }
          
          .donut-segment {
            position: absolute;
            width: 100%;
            height: 100%;
            clip: rect(0, 200px, 200px, 100px);
            border-radius: 50%;
            transform: rotate(var(--start-angle, 0deg));
          }
          
          .donut-segment .circle {
            position: absolute;
            width: 100%;
            height: 100%;
            clip: rect(0, 100px, 200px, 0);
            border-radius: 50%;
            background: var(--color, #ddd);
            transform: rotate(var(--angle, 0deg));
          }

          .donut-hole {
            position: absolute;
            top: 50%;
            left: 50%;
            width: 100px;
            height: 100px;
            background: #fff;
            border-radius: 50%;
            transform: translate(-50%, -50%);
          }
        `}
      </style>
      {data.map((item, index) => {
        const valuePercentage = (item.value / total) * 100;
        const angle = (valuePercentage / 100) * 360;
        const startAngle = index === 0 ? 0 : (data[0].value / total) * 360;

        return (
          <div
            key={item.label}
            className="donut-segment"
            style={{
              '--start-angle': `${startAngle}deg`,
              '--angle': `${angle}deg`,
              '--color': colors[index],
            }}
            title={`${item.label}: ${item.value}`}
          >
            <div className="circle"></div>
          </div>
        );
      })}
      <div className="donut-hole"></div>
    </div>
  );
};

export default DonutChart;

// Usage example
const data = [
  { label: 'Open Incident', value: 10 }, // Replace with opencomplaints?.length
  { label: 'Close Incident', value: 6 } // Replace with closecomplaints?.length
];
