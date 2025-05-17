import React, { useState } from 'react';
import styles from '../styles/TeamSetup.module.css';
import { join } from '../service/AuthService';
import Header from '../components/Header';

const TeamSetup = () => {
  const [teamName, setTeamName] = useState('');
  const [teamCode, setTeamCode] = useState('');
  const [teamPassword, setTeamPassword] = useState('');
  const [joinTeamPassword, setJoinTeamPassword] = useState('');
  
  const handleSubmit = (event) => {
    event.preventDefault();
    // Handle team creation logic here
    console.log('Team name:', teamName);
  };

  // Mock team list (만약 팀이 존재한다면)
  // cost teams = [
  //   { id: 1 , name: 'Team Name 1'},
  //   { id: 2 , name: 'Team Name 2'},
  //   { id: 3 , name: 'Team Name 3'},
  //   { id: 4 , name: 'Team Name 4'},
  //   { id: 5 , name: 'Team Name 5'},
  // ]
  
  // Mock team list
  const teams = []; // Temporarily empty to test noTeamsCard

  return (
    <div>
    <Header/> 
    <div className={styles.teamSetupContainer}>
      <div className={styles.mainContent}>
        <div className={styles.leftColumn}>
          <h1>Team Management</h1> {/* New heading for left column */}
          <form className={styles.formContainer} onSubmit={handleSubmit}>
            <label className={styles.formLabel}>
              Team Name
              <input
                type="text"
                value={teamName}
                onChange={(event) => setTeamName(event.target.value)}
                className={styles.formInput}
              />
            </label>
            <label className={styles.formLabel}>
              Team Password
              <input
                type="text"
                value={teamPassword}
                onChange={(event) => setTeamPassword(event.target.value)}
                className={styles.formInput}
              />
            </label>
            <button type="submit" className={styles.formButton}>Create Team</button>
          </form>
          <form className={styles.formContainer} onSubmit={(event) => {
            event.preventDefault();
            // Handle team joining logic here
            console.log('Team code:', teamCode);
          }}>
            <label className={styles.formLabel}>
              Team Code
              <input
                type="text"
                value={teamCode}
                onChange={(event) => setTeamCode(event.target.value)}
                className={styles.formInput}
              />
            </label>
            <label className={styles.formLabel}>
              Team Password
              <input
                type="text"
                value={joinTeamPassword}
                onChange={(event) => setJoinTeamPassword(event.target.value)}
                className={styles.formInput}
              />
            </label>
            <button type="submit" className={styles.formButton}>Join Team</button>
          </form>
        </div>
        <div className={styles.rightColumn}>
          <div className={styles.teamCardContainer}>
            <h1>Your Teams</h1>
            {teams.length === 0 ? (
              <div className={styles.noTeamsCard}>
                <p>팀을 생성하거나 팀에 참여하세요!</p>
              </div>
            ) : (
              teams.map((team) => (
                <div key={team.id} className={styles.teamCard}>
                  <p>Team Name: {team.name}</p>
                  <a href="/TeamDashboard">Go to Team Workspace</a>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
    </div>
  );
};

export default TeamSetup;
