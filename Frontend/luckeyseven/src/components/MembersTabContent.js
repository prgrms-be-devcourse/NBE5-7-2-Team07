import React from 'react';
import styles from '../styles/MembersTabContent.module.css';

// Placeholder for Members data - In a real app, this would come from an API or state management
const teamMembersData = [
  { id: 1, name: 'Member1', email: 'Member1@example.com', role: 'Leader' },
  { id: 2, name: 'Member2', email: 'Member2@example.com', role: 'Member' },
  { id: 3, name: 'Member3', email: 'Member3@example.com', role: 'Member' },
  // Add more members as needed
];

const MembersTabContent = ({ members }) => {
  return (
      <div className={styles.membersTabContent}>
        <h3 className={styles.membersPageTitle}>Team members</h3>

        <div className={styles.inviteMembersSection}>
          <h4 className={styles.inviteMembersTitle}>Invite Members</h4>
          <div className={styles.inviteDetails}>
            <span>Team Code: 1234</span>
            <span>Password: 7777</span>
          </div>
          <button className={styles.copyInviteButton}>Copy Invite Code</button>
        </div>

        <ul className={styles.membersList}>
          {members.map(member => (
              <li key={member.id} className={styles.memberItem}>
                <div className={styles.memberAvatar}></div>
                <div className={styles.memberInfo}>
                  <span className={styles.memberName}>{member.name}</span>
                  <span className={styles.memberEmail}>{member.email}</span>
                </div>
                <span className={`${styles.memberRole} ${member.role === 'Leader' ? styles.leaderRole : styles.memberRoleTag}`}>
              {member.role}
            </span>
              </li>
          ))}
        </ul>
      </div>
  );
};

export default MembersTabContent;
